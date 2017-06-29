package edu.cmu.cs.lti.deiis.annotators

import edu.cmu.cs.lti.deiis.model.Types
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.Serializer
import org.lappsgrid.serialization.lif.Annotation
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.serialization.lif.View

import static org.lappsgrid.discriminator.Discriminators.Uri
import org.lappsgrid.metadata.ServiceMetadataBuilder

/**
 * @author Keith Suderman
 */
class Tokenizer extends AbstractAnnotator {

    public Tokenizer() {
        super(Tokenizer.class)
    }

    protected ServiceMetadataBuilder configure(ServiceMetadataBuilder builder) {
        builder.name(this.class.name)
                .requireFormat(Uri.LIF)
                .produceFormat(Uri.LIF)
                .produce(Uri.TOKEN)
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

        Container container = new Container((Map) data.payload)
        View view = container.newView("tokens")
        Map contains = [:]
        contains[Uri.TOKEN] = [ producer: this.class.name ]
        view.metadata.contains = contains
        int start = 0
        int id = 0
        container.text.eachLine { String line ->
            int n = line.length() - 1
            String[] tokens = line.substring(0,n).split(' ')
            tokens.each { String token ->
                Annotation a = view.newAnnotation("tok-${id++}", Uri.TOKEN)
                a.start = start
                a.end = start + token.length()
                start = a.end + 1
                Map f = a.features
                f.string = token
                f.componentId = this.class.name
                f.confidence = 1.0f
            }
            Annotation period = view.newAnnotation("tok-${id++}", Types.PUNCTUATION)
            period.start = start - 1
            period.end = start
            period.features.string = '.'
            period.features.componentId = this.class.name
            period.features.confidence = 1.0f
            ++start
        }
        data.payload = container
        return data.asPrettyJson()
    }
}
