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
