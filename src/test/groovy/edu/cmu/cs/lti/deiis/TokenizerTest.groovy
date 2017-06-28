package edu.cmu.cs.lti.deiis

import edu.cmu.cs.lti.deiis.annotators.Tokenizer
import groovy.json.JsonOutput
import org.junit.*
import org.lappsgrid.api.WebService
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.Serializer
import org.lappsgrid.serialization.lif.Annotation
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.serialization.lif.View

import static org.junit.Assert.*

import static org.lappsgrid.discriminator.Discriminators.*


/**
 * @author Keith Suderman
 */
class TokenizerTest {

    WebService service

    @Before
    void setup() {
        service = new Tokenizer()
    }

    @After
    void teardown() {
        service = null
    }

    @Test
    void testTokenizer() {
        Data data = new Data(Uri.LIF)
        Container container = new Container()
        container.text = '''one two three.
four five six.
seven eight nine ten.'''
        data.payload = container

        String json = service.execute(data.asJson())
        println JsonOutput.prettyPrint(json)
        data = Serializer.parse(json, Data)
        assert Uri.LIF == data.discriminator
        container = new Container((Map)data.payload)
        assert 1 == container.views.size()
        View view = container.views[0]
        assert view.metadata.contains[Uri.TOKEN] != null
        assert 13 == view.annotations.size()
        Iterator<Annotation> it = view.annotations.iterator()
        'one two three . four five six . seven eight nine ten .'.split(' ').each { String word ->
            println word
            assert it.hasNext()
            Annotation a = it.next()
            String string = container.text.substring((int)a.start, (int)a.end)
            assert word == a.features.string
            assert word == string
        }
    }
}
