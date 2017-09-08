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

import edu.cmu.cs.lti.deiis.annotators.*
import org.junit.Test
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.DataContainer
import org.lappsgrid.serialization.Serializer
import org.lappsgrid.serialization.lif.Container

import static org.junit.Assert.*


/**
 * @author Keith Suderman
 */
class NGramAnnotatorTest {

    @Test
    void parameterTest() {
        String text = '''Q What is the meaning of life?
A 1 42.
A 0 Money and power.
'''
        String json = new PrepareData().execute(text)
        json = new Tokenizer().execute(json)

        Data d = Serializer.parse(json)
        if (d.parameters == null) {
            d.parameters = [:]
        }
        d.parameters['type'] = 'BIGRAM'
        json = new NGramAnnotator().execute(d.asJson())
        d = Serializer.parse(json, DataContainer)
        println d.asPrettyJson()

    }

}
