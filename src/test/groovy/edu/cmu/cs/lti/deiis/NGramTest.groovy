package edu.cmu.cs.lti.deiis

import edu.cmu.cs.lti.deiis.model.NGram
import org.junit.Test

/**
 * @author Keith Suderman
 */
class NGramTest {

    @Test
    void testUnigram() {
        NGram ngram = new NGram(1)
        assert 0 == ngram.size()
        assert '' == ngram.toString()
        ngram.add('foo')
        assert 1 == ngram.size()
        assert 'foo' == ngram.toString()
        ngram.add('bar')
        assert 1 == ngram.size()
        assert 'bar' == ngram.toString()
    }

    @Test
    void testBiGram() {
        NGram ngram = new NGram(2)
        ngram << 'foo'
        ngram << 'bar'
        assert 2 == ngram.size()
        assert 'foo bar' == ngram.toString()
        ngram << 'baz'
        assert 2 == ngram.size()
        assert 'bar baz' == ngram.toString()
    }

    @Test
    void testTriGram() {
        NGram ngram = new NGram(3)
        ngram << '1'
        ngram << '2'
        ngram << '3'
        assert 3 == ngram.size()
        assert '1 2 3' == ngram.toString()
        ngram << '4'
        assert 3 == ngram.size()
        assert '2 3 4' == ngram.toString()
        ngram.add('5')
        ngram.add('6')
        assert 3 == ngram.size()
        assert '4 5 6' == ngram.toString()
    }
}
