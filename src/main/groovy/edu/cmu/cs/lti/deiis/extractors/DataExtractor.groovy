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

package edu.cmu.cs.lti.deiis.extractors

import edu.cmu.cs.lti.deiis.annotators.AbstractService
import edu.cmu.cs.lti.deiis.model.Types
import groovy.util.logging.Slf4j
import org.lappsgrid.discriminator.Discriminators
import org.lappsgrid.metadata.ServiceMetadataBuilder
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.Serializer
import org.lappsgrid.serialization.lif.Container

@Slf4j("logger")
class DataExtractor extends AbstractService {
    DataExtractor() {
        super(DataExtractor)
    }

    @Override
    protected ServiceMetadataBuilder configure(ServiceMetadataBuilder builder) {
        return builder.name(this.class.name)
            .require(Types.PRF)
            .produceFormat('csv')
    }

    @Override
    String execute(String input) {
        Data data = Serializer.parse(input)
        if (Discriminators.Uri.ERROR == data.discriminator) {
            logger.warn("Received an ERROR data package.")
            return input
        }
        if (Discriminators.Uri.LIF != data.discriminator) {
            logger.warn("Received unsupported discriminator {}", data.discriminator)
            return unsupported(data.discriminator)
        }
        Container container = new Container((Map) data.payload)
        if (container.metadata == null || container.metadata.ranking == null) {
            logger.error("No ranking data found")
            return 'No ranking data found.'
        }
        float p = container.metadata.precision as Float
        float r = container.metadata.recall as Float
        float f = container.metadata.fscore as Float
        int i = container.metadata.ngram as Integer
        int n = container.metadata.n as Integer
        return String.format("%d, %d, %1.3f, %1.3f, %1.3f", i, n, p, r, f)
    }
}
