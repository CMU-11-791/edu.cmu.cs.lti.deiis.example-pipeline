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

import edu.cmu.cs.lti.deiis.annotators.PrepareData
import edu.cmu.cs.lti.deiis.model.Types
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.lappsgrid.api.WebService
import org.lappsgrid.discriminator.Discriminators
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.DataContainer
import org.lappsgrid.serialization.Serializer
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.serialization.lif.View

import java.lang.annotation.Annotation

/**
 * @author Keith Suderman
 */
class PrepareDataTest {

    WebService service

    @Before
    void setup() {
        service = new PrepareData()
    }

    @After
    void teardown() {
        service = null
    }

//    @Test
//    void testPrepare() {
//    }

    @Test
    void testFindViewsThatContain() {
        InputStream stream = this.class.classLoader.getResourceAsStream("booth.txt")
        assert stream != null
        Data data = new Data(Discriminators.Uri.TEXT, stream.text)
        String json = service.execute(data.asJson())
        DataContainer dc = Serializer.parse(json, DataContainer)
        Container container = dc.payload
        List<View> views = container.findViewsThatContain(Types.QUESTION)
        assert 1 == views.size()
        View questionView = views[0]
        List<Annotation> list = questionView.findByAtType(Types.QUESTION)
        assert 1 == list.size()

        views = container.findViewsThatContain(Types.ANSWER)
        assert 1 == views.size()
        View answerView = views[0]
        assert questionView.is(answerView)
    }

    @Test
    void testTest() {
        Container container = new Container()
        List<View> views = container.findViewsThatContain("foobar")
        assert views != null
        assert 0 == views.size()
    }
}
