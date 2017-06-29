package edu.cmu.cs.lti.deiis.scoring

import edu.cmu.cs.lti.deiis.annotators.AbstractAnnotator
import edu.cmu.cs.lti.deiis.error.StateError
import edu.cmu.cs.lti.deiis.model.NGram
import edu.cmu.cs.lti.deiis.model.Types
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.lappsgrid.metadata.ServiceMetadataBuilder
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.Serializer
import org.lappsgrid.serialization.lif.Annotation
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.serialization.lif.View

import static org.lappsgrid.discriminator.Discriminators.*

/**
 * @author Keith Suderman
 */
@Slf4j("logger")
class NGramScorer extends AbstractAnnotator {

    NGram.Type type

    public NGramScorer() {
        super(NGramScorer.class)
    }

    public NGramScorer(NGram.Type type) {
        super(NGramScorer.class)
        this.type = type
    }

    protected ServiceMetadataBuilder configure(ServiceMetadataBuilder builder) {
        builder.name(this.class.name)
                .produce(Types.SCORE)
                .requireFormat(Uri.LIF)
                .produceFormat(Uri.LIF)
        return builder
    }

    String execute(String input) {
        Data data = Serializer.parse(input, Data)
        if (Uri.ERROR == data.discriminator) {
            return input
        }
        if (Uri.LIF != data.discriminator) {
            return unsupported(data.discriminator)
        }
        if (data.parameters?.type) {
            type = NGram.Type.valueOf((String)data.parameters.type)
        }
        if (type == null) {
            return error("The type of NGram to score has not been selected.")
        }

        String json
        Container container = new Container((Map) data.payload)
        try {
            json = process(container)
        }
        catch(StateError e) {
            return error(e.message)
        }

        return json
    }

    protected String process(Container container) throws StateError {
        List<Annotation> ngrams = findAnnotations(container, Types.NGRAM)

        List<Annotation> list = findAnnotations(container, Types.QUESTION)
        List<String> question = getNgrams(list[0], ngrams)

        List<Annotation> answers = findAnnotations(container, Types.ANSWER)
        answers.each { Annotation answer ->
            logger.debug("Scoring answer {}", answer.id)

            answer.features.score = score(question, answer, ngrams)
            logger.debug("Type {} Score {}", type, answer.features.score)
        }
        return new Data(Uri.LIF, container).asPrettyJson()
    }

    protected List<String> getNgrams(Annotation span, List<Annotation> annotations) {
        return (List<String>) annotations.findAll { span.start <= it.start && it.end <= span.end }.collect { it.features.text }
    }

    protected float score(List<String> question, Annotation answer, List<Annotation> ngrams) {
        int count = 0
        List<String> answerNgrams = getNgrams(answer, ngrams)
        answerNgrams.each { String ngram ->
            if (question.contains(ngram)) {
                ++count
            }
        }
        return answer.features.score = count / question.size()
    }

    protected List<Annotation> findAnnotations(Container container, String type) {
        View view = container.views.find { it.metadata.contains[type] }
        return view.annotations.findAll { it.atType == type }
    }
}
