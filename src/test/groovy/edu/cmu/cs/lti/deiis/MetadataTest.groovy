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

package edu.cmu.cs.lti.deiis

import edu.cmu.cs.lti.deiis.annotators.Tokenizer
import edu.cmu.cs.lti.deiis.model.Types
import org.junit.Test
import org.lappsgrid.api.WebService
import org.lappsgrid.metadata.ServiceMetadata
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.Serializer
import org.lappsgrid.serialization.lif.Annotation

import static org.junit.Assert.*

import static org.lappsgrid.discriminator.Discriminators.*

/**
 * @author Keith Suderman
 */
class MetadataTest {

    @Test
    void tokenizer() {
        ServiceMetadata md = getMetadata(Tokenizer)
        checkCommonMetadata(md)
        assert Tokenizer.class.name == md.getName()
        checkProduces(md, Uri.TOKEN)
    }

    void checkProduces(ServiceMetadata metadata, String... args) {
        checkAnnotations(metadata.getProduces().annotations, args)
    }

    void checkRequires(ServiceMetadata metadata, String... args) {
        checkAnnotations(metadata.getRequires().annotations, args)
    }

    void checkAnnotations(List<Annotation> annotations, String... args) {
        assert args.size() == annotations.size()
        args.each {
            assert annotations.contains(it)
        }
    }

    void checkCommonMetadata(ServiceMetadata md) {
        assert '1.0.0' == md.getVersion()
        assert 'https://www.lti.cs.cmu.edu' == md.getVendor()
    }

    ServiceMetadata getMetadata(Class<? extends WebService> aClass) {
        WebService service = (WebService) aClass.newInstance()
        String json = service.getMetadata()
        Data data = Serializer.parse(json, Data)
        return new ServiceMetadata((Map) data.payload)
    }
}

