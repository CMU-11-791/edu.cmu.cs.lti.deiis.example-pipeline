package edu.cmu.cs.lti.deiis.model

import org.lappsgrid.discriminator.Discriminators

/**
 * @author Keith Suderman
 */
class Types extends Discriminators.Uri {
    static final String QUESTION = 'http://deiis.lti.cs.cmu.edu/Question'
    static final String ANSWER = 'http://deiis.lti.cs.cmu.edu/Answer'
    static final String PUNCTUATION = 'http://deiis.lti.cs.cmu.edu/Punctuation'
    static final String NGRAM = 'http://deiis.lti.cs.cmu.edu/NGram'
    static final String SCORE = 'http://deiis.lti.cs.cmu.edu/Answer#score'
    static final String RANKED = 'http://deiis.lti.cs.cmu.edu/Ranked'
    static final String PRF = 'http://deiis.lti.cs.cmu.edu/PrecisionRecallF1'


}
