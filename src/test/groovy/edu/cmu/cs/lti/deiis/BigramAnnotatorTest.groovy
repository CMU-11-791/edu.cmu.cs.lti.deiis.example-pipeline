package edu.cmu.cs.lti.deiis

import edu.cmu.cs.lti.deiis.annotators.NGramAnnotator
import edu.cmu.cs.lti.deiis.annotators.PrepareData
import edu.cmu.cs.lti.deiis.annotators.Tokenizer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.lappsgrid.api.WebService
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.Serializer

import static org.lappsgrid.discriminator.Discriminators.*

/**
 * @author Keith Suderman
 */
class BigramAnnotatorTest {

    @Test
    void testBigram() {
        String text = this.class.getResourceAsStream("/booth.txt").text
        Data data = new Data(Uri.TEXT, text)
        String json = new PrepareData().execute(data.asJson())
        json = new Tokenizer().execute(json)
        json = new NGramAnnotator(2).execute(json)
        data = Serializer.parse(json, Data)
        println data.asPrettyJson()
    }
}
