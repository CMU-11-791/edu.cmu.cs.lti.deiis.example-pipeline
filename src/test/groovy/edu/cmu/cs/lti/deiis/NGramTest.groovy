package edu.cmu.cs.lti.deiis

import edu.cmu.cs.lti.deiis.model.NGram
import org.junit.Test
import org.lappsgrid.serialization.lif.Annotation

/**
 * @author Keith Suderman
 */
class NGramTest {

    @Test
    void testUnigram() {
        NGram ngram = new NGram(1)
        assert 0 == ngram.size()
        assert '' == ngram.toString()
        ngram.add(token('foo'))
        assert 1 == ngram.size()
        assert 'foo' == ngram.toString()
        ngram.add(token('bar'))
        assert 1 == ngram.size()
        assert 'bar' == ngram.toString()
    }

    @Test
    void testBiGram() {
        NGram ngram = new NGram(2)
        ngram << token('foo')
        ngram << token('bar')
        assert 2 == ngram.size()
        assert 'foo bar' == ngram.toString()
        ngram << token('baz')
        assert 2 == ngram.size()
        assert 'bar baz' == ngram.toString()
    }

    @Test
    void testTriGram() {
        NGram ngram = new NGram(3)
        ngram << token('1')
        ngram << token('2')
        ngram << token('3')
        assert 3 == ngram.size()
        assert '1 2 3' == ngram.toString()
        ngram << token('4')
        assert 3 == ngram.size()
        assert '2 3 4' == ngram.toString()
        ngram.add(token('5'))
        ngram.add(token('6'))
        assert 3 == ngram.size()
        assert '4 5 6' == ngram.toString()
    }

    Annotation token(String text) {
        Annotation a = new Annotation()
        a.features.string = text
        return a
    }
}
