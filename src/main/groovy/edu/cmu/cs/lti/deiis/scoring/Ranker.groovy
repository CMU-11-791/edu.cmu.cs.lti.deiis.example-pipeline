package edu.cmu.cs.lti.deiis.scoring

import edu.cmu.cs.lti.deiis.annotators.AbstractAnnotator
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
 * Ranks (sorts) a set of Answer annotations based on a given feature name. The
 * feature name can be provided in the constructor or passed in the data parameters.
 * A feature name provided in the data parameters takes precedence over a feature
 * name provided in the constructor.
 *
 * @author Keith Suderman
 */
@Slf4j('logger')
class Ranker extends AbstractAnnotator {

    /**
     * The feature to sort on.
     */
    String feature

    public Ranker() {
        this("score")
    }

    public Ranker(String feature) {
        super(Ranker.class)
        this.feature = feature
    }

    protected ServiceMetadataBuilder configure(ServiceMetadataBuilder builder) {
        builder.name(this.class.name)
        return builder
    }

    String execute(String input) {
        Data data = Serializer.parse(input, Data)
        if (Uri.ERROR == data.discriminator) {
            logger.warn("Received an ERROR data package.")
            return input
        }
        if (Uri.LIF != data.discriminator) {
            logger.warn("Received unsupported discriminator {}", data.discriminator)
            return unsupported(data.discriminator)
        }
//        if (data.parameters?.feature) {
//            this.feature = data.parameters.feature
//            logger.info("Feature found in data.parameters: {}", this.feature)
//        }
//
//        if (feature == null) {
//            logger.error("No feature name has been provided.")
//            return error("No feature name has been provided.")
//        }

        Container container = new Container((Map)data.payload)
        List<View> views = container.findViewsThatContain(Types.ANSWER)
        if (views.size() == 0) {
            return error("No views contain Answer annotations")
        }
        View view = views[0]
        List<Annotation> answers = view.annotations.findAll { it.atType == Types.ANSWER }
        if (answers.size() == 0) {
            return error("No answers found.")
        }
        StringWriter stringWriter = new StringWriter()
        PrintWriter writer = new PrintWriter(stringWriter)
//        logger.debug("Sorting candidate answers by feature {}", feature)
        answers.sort { it.features.score ?: 0 }.reverse().each { answer ->
            logger.debug("Writing answer {}", answer.id)
            if (answer.features.isAnswer) {
                writer.print '1 '
            }
            else {
                writer.print '0 '
            }

            writer.print answer.features.score
            writer.print ' '
            writer.println answer.features.text
        }
        data.discriminator = Types.LAPPS
        data.payload = stringWriter.toString()
        return data.asPrettyJson()
    }
}
