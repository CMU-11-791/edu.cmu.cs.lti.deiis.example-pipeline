package edu.cmu.cs.lti.deiis.annotators

import org.lappsgrid.api.WebService
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.Serializer

import static org.lappsgrid.discriminator.Discriminators.*
import org.lappsgrid.metadata.ServiceMetadata
import org.lappsgrid.metadata.ServiceMetadataBuilder


/**
 * @author Keith Suderman
 */
abstract class AbstractAnnotator implements WebService {

    static final String UNSUPPORTED = "Unsupported discriminator type: "
    private String metadata
    private String name

    public AbstractAnnotator(Class child) {
        name = child.name
    }

    protected abstract ServiceMetadataBuilder configure(ServiceMetadataBuilder builder)

    String getMetadata() {
        if (metadata == null) {
            ServiceMetadata md = configure(new ServiceMetadataBuilder())
                .version("1.0.0")
                .license(Uri.APACHE2)
                .allow(Uri.ANY)
                .vendor("https://www.lti.cs.cmu.edu")
                .requireFormat(Uri.LIF)
                .produceFormat(Uri.LIF)
                .build()
            Data data = new Data(Uri.META, md)
            metadata = data.asPrettyJson()
        }
        return metadata
    }

    protected String unsupported(String type) {
        return error(UNSUPPORTED + type)
    }

    protected String error(String message) {
        return new Data(Uri.ERROR, name + ': ' + message).asPrettyJson()
    }
}
