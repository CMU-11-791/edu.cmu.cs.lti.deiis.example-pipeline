/*
 * Copyright (c) 2017. Carnegie Mellon University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package edu.cmu.cs.lti.deiis.annotators

import org.lappsgrid.api.WebService
import org.lappsgrid.serialization.Data

import static org.lappsgrid.discriminator.Discriminators.*
import org.lappsgrid.metadata.ServiceMetadataBuilder


/**
 * The base class for services in the example pipeline with a few helper
 * methods common to all the services.  In particular much of the metadata
 * returned by services is the same.
 *
 */
abstract class AbstractService implements WebService {

    /**
     * Message returned when a service receives a data type it doesn't
     * understand.
     */
    static final String UNSUPPORTED = "Unsupported discriminator type: "

    /**
     * The JSON for the metadata is lazily initialized the first time
     * getMetadata is called.
     */
    private String metadata

    /**
     * Set by sub-classes and used when generating error messages.
     */
    private String name

    public AbstractService(Class child) {
        name = child.name
    }

    /**
     * The configure method is called to allow sub-classes to tailor the
     * metadata returned by the getMetadata() method.
     *
     * @param builder The ServiceMetadataBuilder to configure
     * @return the same builder that was passed as a parameter
     */
    protected abstract ServiceMetadataBuilder configure(ServiceMetadataBuilder builder)

    /**
     * The base class can generate most of the metadata for the service and
     * the configure method will be called so sub-classes have an opportunity
     * to contribute metadata as well.
     *
     * @return JSON representation of the metadata for this service
     */
    String getMetadata() {
        if (metadata == null) {
            ServiceMetadataBuilder builder = new ServiceMetadataBuilder()
                .version("1.0.0")
                .license(Uri.APACHE2)
                .allow(Uri.ANY)
                .vendor("https://www.lti.cs.cmu.edu")
                .requireFormat(Uri.LIF)
                .produceFormat(Uri.LIF)
            configure(builder)
            Data data = new Data(Uri.META, builder.build())
            metadata = data.asPrettyJson()
        }
        return metadata
    }

    /**
     * Generates the JSON serialization of a Data object with the
     * discriminator set to ERROR and the payload set to the
     * UNSUPPORTED message above.
     *
     * @param type the discriminator that is not supported
     * @return the JSON representation of a Data object.
     */
    protected String unsupported(String type) {
        return error(UNSUPPORTED + type)
    }

    /**
     * Generates the JSON serialization of a Data object with the
     * discriminator set to ERROR and the payload set to the
     * supplied message.
     *
     * @param message
     * @return
     */
    protected String error(String message) {
        return new Data(Uri.ERROR, name + ': ' + message).asPrettyJson()
    }
}
