package edu.cmu.cs.lti.deiis.annotators

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
class PrepareData extends AbstractAnnotator {

    public PrepareData() {
        super(PrepareData.class)
    }

    protected ServiceMetadataBuilder configure(ServiceMetadataBuilder builder) {
        builder.name(this.class.name)
                .requireFormat("text/plain")
                .produceFormat(Uri.LIF)
        return builder
    }

    String execute(String input) {
//        Data data = Serializer.parse(input, Data)
//        if (Uri.ERROR == data.discriminator) {
//            return input
//        }
//
//        String text
//        if (Uri.LIF == data.discriminator) {
//            logger.debug("Data is LIF")
//            text = new Container((Map)data.payload).text
//        }
//        else if (Uri.TEXT == data.discriminator) {
//            logger.debug("Data is TEXT")
//            text = data.payload.toString()
//        }
//        else {
//            logger.error("Unsupported data type: {}", data.discriminator)
//            return unsupported(data.discriminator)
//        }

        Container container = new Container();
        View view = container.newView("qa")
        view.metadata.contains = [:]
        view.metadata.contains[Types.QUESTION] = [
            producer: this.class.name,
            type: 'question'
        ]
        view.metadata.contains[Types.ANSWER] = [
            producer: this.class.name,
            type: 'answer'
        ]
        StringWriter buffer = new StringWriter()
        PrintWriter writer = new PrintWriter(buffer)
        Iterator<String> it = input.readLines().iterator()
        String questionText = it.next()
        if (questionText.startsWith('Q ')) {
            questionText = questionText.substring(2)
        }
        else {
            logger.warn("Malformed question does not begin with 'Q'")
        }

        writer.println(questionText)
        Annotation a = view.newAnnotation("q-0", Types.QUESTION)
        a.start = 0
        a.end = questionText.length()
        a.features.componentId = this.class.name
        a.features.confidence = 1.0f
        a.features.text = questionText

        int start = a.end + 1
        int id = 0
        while (it.hasNext()) {
            String line = it.next()
            logger.debug("Line: {}", line)
            if (line == null || line.length() == 0) continue

            boolean isAnswer = line.substring(2, 3) == '1'
            String answerText = line.substring(4)
            writer.println(answerText)
            a = view.newAnnotation("a-${id++}", Types.ANSWER)
            a.start = start
            a.end = start + answerText.length()
            Map f = a.features
            f.componentId = this.class.name
            f.confidence = 1.0f
            f.text = answerText
            f.isAnswer = isAnswer
            logger.debug("A {}: {}", a.id, a.features.text)
            start = a.end + 1
        }
        logger.debug("Packaging return data")
        container.text = buffer.toString()
        Data data = new Data(Uri.LIF, container)
//        data.discriminator = Uri.LIF
//        data.payload = container
        return data.asPrettyJson()
    }
}
