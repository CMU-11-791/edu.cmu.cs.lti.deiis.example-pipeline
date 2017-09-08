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

package edu.cmu.cs.lti.deiis.model

import org.lappsgrid.discriminator.Discriminators

class Types extends Discriminators.Uri {
    static final String QUESTION = 'http://deiis.lti.cs.cmu.edu/Question'
    static final String ANSWER = 'http://deiis.lti.cs.cmu.edu/Answer'
    static final String PUNCTUATION = 'http://deiis.lti.cs.cmu.edu/Punctuation'
    static final String NGRAM = 'http://deiis.lti.cs.cmu.edu/NGram'
    static final String SCORE = 'http://deiis.lti.cs.cmu.edu/Answer#score'
    static final String RANK = 'http://deiis.lti.cs.cmu.edu/Answer#rank'
    static final String PRF = 'http://deiis.lti.cs.cmu.edu/metadata/PrecisionRecallF1'
}
