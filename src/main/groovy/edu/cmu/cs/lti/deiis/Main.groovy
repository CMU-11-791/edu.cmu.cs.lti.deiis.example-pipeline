package edu.cmu.cs.lti.deiis

import edu.cmu.cs.lti.deiis.annotators.NGramAnnotator
import edu.cmu.cs.lti.deiis.annotators.PrepareData
import edu.cmu.cs.lti.deiis.annotators.Tokenizer
import edu.cmu.cs.lti.deiis.model.NGram
import edu.cmu.cs.lti.deiis.scoring.Evaluator
import edu.cmu.cs.lti.deiis.scoring.NGramScorer
import edu.cmu.cs.lti.deiis.scoring.Ranker
import org.lappsgrid.serialization.Serializer

import static org.lappsgrid.discriminator.Discriminators.*
import org.lappsgrid.serialization.Data

/**
 * @author Keith Suderman
 */
class Main {

    static void main(String[] args) {
        InputStream stream = Main.class.getResourceAsStream("/booth.txt")
//        Data data = new Data(Uri.TEXT, stream.text)

        NGram.Type type = NGram.Type.BIGRAM

        Pipeline pipeline = new Pipeline()
        pipeline << new PrepareData()
        pipeline << new Tokenizer()
        pipeline << new NGramAnnotator(type)
        pipeline << new NGramScorer(type)
        pipeline << new Ranker(type.toString())
        pipeline << new Evaluator(2)
        String text = pipeline.execute(stream.text)
        if (text.startsWith("{")) {
            println groovy.json.JsonOutput.prettyPrint(text)
        }
        else {
            println text
        }
    }
}
