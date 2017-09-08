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
import edu.cmu.cs.lti.deiis.model.NGram
import edu.cmu.cs.lti.deiis.extractors.DataExtractor
import edu.cmu.cs.lti.deiis.scoring.Evaluator
import edu.cmu.cs.lti.deiis.scoring.NGramScorer
import edu.cmu.cs.lti.deiis.scoring.Ranker
import edu.cmu.cs.lti.deiis.extractors.RankingExtractor

import static edu.cmu.cs.lti.deiis.model.NGram.Type.*

class Main {

    Pipeline create(NGram.Type type, int n) {
        Pipeline pipeline = new Pipeline()
        pipeline << new PrepareData()
        pipeline << new Tokenizer()
        pipeline << new NGramAnnotator(type)
        pipeline << new NGramScorer(type)
        pipeline << new Ranker('score')
        pipeline << new Evaluator(n)
        pipeline << new RankingExtractor()
//        pipeline << new DataExtractor()
        return pipeline
    }

    Pipeline all3() {
        Pipeline pipeline = new Pipeline()
        pipeline << new PrepareData()
        pipeline << new Tokenizer()
        [UNIGRAM, BIGRAM, TRIGRAM].each { type ->
            pipeline << new NGramAnnotator(type)
            pipeline << new NGramScorer(type)
        }
        pipeline << new Ranker('score')
        pipeline << new Evaluator(-1)
        pipeline << new RankingExtractor()
//        pipeline << new DataExtractor()
    }

    void matrix() {
        String input = Main.class.getResourceAsStream("/booth.txt").text
        [UNIGRAM, BIGRAM, TRIGRAM].each { type ->
            [1,2,3,-1].each { n ->
                Pipeline pipeline = create(type, n)
//                pipeline << new RankingExtractor()
                pipeline << new DataExtractor()
                println pipeline.execute(input)
            }
        }
    }

    void run() {
        String input = Main.class.getResourceAsStream("/booth.txt").text
//        String json = create(BIGRAM, 1).execute(input)
        String json = all3().execute(input)
        println json
//        println new DataExtractor().execute(json)
//        println new RankingExtractor().execute(json)
    }

    static void main(String[] args) {
        new Main().run()
    }
}
