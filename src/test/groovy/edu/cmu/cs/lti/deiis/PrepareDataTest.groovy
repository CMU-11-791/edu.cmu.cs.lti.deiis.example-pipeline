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
