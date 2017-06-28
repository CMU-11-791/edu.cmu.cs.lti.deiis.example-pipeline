package edu.cmu.cs.lti.deiis.scoring

import edu.cmu.cs.lti.deiis.annotators.AbstractAnnotator
import edu.cmu.cs.lti.deiis.error.StateError
import edu.cmu.cs.lti.deiis.model.NGram
import edu.cmu.cs.lti.deiis.model.Types
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
//        this.type = NGram.Type.BIGRAM
    }

    public NGramScorer(NGram.Type type) {
        super(NGramScorer.class)
        this.type = type
    }

    protected ServiceMetadataBuilder configure(ServiceMetadataBuilder builder) {
        builder.name(this.class.name)
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
            type = NGram.Type.valueOf(data.parameters.type)
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
        View qaView = findView(container, Types.QUESTION)
        Annotation question = qaView.annotations.find { it.atType == Types.QUESTION }
        List<String> inQuestion = question.features[type.toString()].ngrams

        View answerView = findView(container, Types.ANSWER)
        List<Annotation> answers = answerView.annotations.findAll { it.atType == Types.ANSWER }
        answers.each { Annotation answer ->
            logger.debug("Scoring answer {}", answer.id)
            int score = 0
            answer.features[type.toString()].ngrams.each { String ngram ->
                if (inQuestion.contains(ngram)) {
                    ++score
                }
            }
            double value = score / inQuestion.size()
            answer.features.score = value //score / inQuestion.size()
            logger.debug("Type {} Score {}", type, answer.features.score)
        }
        return new Data(Uri.LIF, container).asPrettyJson()
    }

    protected View findView(Container container, String type) {
        List<View> views = container.findViewsThatContain(type)
        if (views == null || views.size() == 0) {
            throw new StateError("No views of type $type found.")
        }
        return views[0]
    }
}
