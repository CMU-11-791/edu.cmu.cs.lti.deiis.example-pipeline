package edu.cmu.cs.lti.deiis.annotators

import edu.cmu.cs.lti.deiis.model.NGram
import edu.cmu.cs.lti.deiis.model.Types
import groovy.util.logging.Slf4j
import org.lappsgrid.serialization.lif.Annotation
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.serialization.lif.View

import static org.lappsgrid.discriminator.Discriminators.*
import org.lappsgrid.metadata.ServiceMetadataBuilder
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.Serializer

/**
 * @author Keith Suderman
 */
@Slf4j("logger")
class NGramAnnotator extends AbstractAnnotator {

    private NGram.Type type
    private int id

    public NGramAnnotator() {
        super(NGramAnnotator.class)
    }

    public NGramAnnotator(NGram.Type type) {
        super(NGramAnnotator.class)
        this.type = type
    }

    public NGramAnnotator(int size) {
        super(NGramAnnotator.class)
        type = NGram.Type.valueOf(size)
    }

    protected ServiceMetadataBuilder configure(ServiceMetadataBuilder builder) {
        builder.name(this.class.name)
                .requireFormat(Uri.LIF)
                .produceFormat(Uri.LIF)
                .produce(Types.NGRAM)
        return builder
    }

    String execute(String input) {
        Data data = Serializer.parse(input,Data)
        if (Uri.ERROR == data.discriminator) {
            return input
        }
        if (Uri.LIF != data.discriminator) {
            return unsupported(data.discriminator)
        }
        if (data.parameters?.type) {
            logger.info("type set in data.parameters: {}", data.parameters.type )
            type = NGram.Type.valueOf(data.parameters.type)
        }
        if (type == null) {
            logger.error("NGram type has not been specified")
            return error("NGram type has not been specified")
        }
        logger.info("Creating NGrams of type: {}", type)
        Container container = new Container((Map) data.payload)

        List<Annotation> answers = find(container, Types.ANSWER)
        View view = container.newView()
        view.addContains(Types.NGRAM, this.class.name, type.toString())

        id = 0
        answers.each { Annotation answer ->
            createNGrams(container, view, answer)
        }
        find(container, Types.QUESTION).each { Annotation question ->
            createNGrams(container, view, question)
        }

        data.payload = container
        return data.asPrettyJson()
    }

    protected boolean tokenInSpan(Annotation a , Annotation span) {
        return a.atType == Uri.TOKEN && span.start <= a.start && a.end <= span.end
    }

    protected void createNGrams(Container container, View scoreView, Annotation span) {
        List<View> views = container.findViewsThatContain(Uri.TOKEN)
        if (views.size() < 1) {
            return
        }
        View view = views[0]
        // Find all the annotation covered by the span.
        List<Annotation> tokens = view.annotations.findAll { tokenInSpan(it, span) }
        Iterator<Annotation> it = tokens.iterator()
        List<String> ngrams = []
        NGram ngram = new NGram(type)
        // Prime the ngram with the first n-1 tokens
        int n = type.n() - 1
        while (n > 0 && it.hasNext()) {
            ngram << it.next() //.features.string
            --n
        }

        while (it.hasNext()) {
            ngram << it.next() //.features.string
            ngrams << ngram.toString()
            Annotation a = ngram.annotate(type.toString() + "-" + (++id))
            scoreView.add(a)
        }
//        logger.debug("Setting feature {} on span {}", type, span.id)
//        span.features[type] = [score:0, rank: 0, ngrams: ngrams]
    }

    protected List<Annotation> find(Container container, String type) {
        List<Annotation> result = null
        List<View> views = container.findViewsThatContain(type)
        if (views.size() == 0) {
            return result
        }
        return views[0].annotations.findAll{ it.atType == type }
    }
}
