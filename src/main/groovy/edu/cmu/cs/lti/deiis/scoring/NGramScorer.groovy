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

package edu.cmu.cs.lti.deiis.scoring

import edu.cmu.cs.lti.deiis.annotators.AbstractService
import edu.cmu.cs.lti.deiis.error.StateError
import edu.cmu.cs.lti.deiis.model.NGram
import edu.cmu.cs.lti.deiis.model.Types
import groovy.util.logging.Slf4j
import org.lappsgrid.metadata.ServiceMetadataBuilder
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.Serializer
import org.lappsgrid.serialization.lif.Annotation
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.serialization.lif.View

import static org.lappsgrid.discriminator.Discriminators.*

@Slf4j("logger")
class NGramScorer extends AbstractService {

    NGram.Type type

    public NGramScorer() {
        super(NGramScorer.class)
    }

    public NGramScorer(NGram.Type type) {
        super(NGramScorer.class)
        this.type = type
    }

    protected ServiceMetadataBuilder configure(ServiceMetadataBuilder builder) {
        return builder
                .name(this.class.name)
                .produce(Types.SCORE)
                .require(Types.NGRAM)
    }

    String execute(String input) {
        Data data = Serializer.parse(input, Data)
        if (Uri.ERROR == data.discriminator) {
            return input
        }
        if (Uri.LIF != data.discriminator) {
            return unsupported(data.discriminator)
        }
        if (data.parameters?.type) {
            type = NGram.Type.valueOf((String)data.parameters.type)
        }
        if (type == null) {
            return error("The type of NGram to score has not been selected.")
        }

        String json
        Container container = new Container((Map) data.payload)
        try {
            Map parameters = data.parameters
            data = process(container)
            data.parameters = parameters
        }
        catch(StateError e) {
            return error(e.message)
        }

        return data.asPrettyJson()
    }

    protected Data process(Container container) throws StateError {
        List<Annotation> ngrams = findAnnotations(container, Types.NGRAM)

        List<Annotation> list = findAnnotations(container, Types.QUESTION)
        List<String> question = getNgrams(list[0], ngrams)

        List<Annotation> answers = findAnnotations(container, Types.ANSWER)
        answers.each { Annotation answer ->
            if (answer.features.score == null) {
                answer.features.score = 0.0f
            }
//            answer.features.score += score(question, answer, ngrams)
            score(question, answer, ngrams)
            logger.info("Answer {} Score {}", answer.id, answer.features.score)
        }
        return new Data(Uri.LIF, container) //.asPrettyJson()
    }

    protected List<String> getNgrams(Annotation span, List<Annotation> annotations) {
        return (List<String>) annotations.findAll { span.start <= it.start && it.end <= span.end }.collect { it.features.text }
    }

    protected float score(List<String> question, Annotation answer, List<Annotation> ngrams) {
        logger.trace("Answer Text: {}", answer.features.text)
        int count = 0
        List<String> answerNgrams = getNgrams(answer, ngrams)
        answerNgrams.each { String ngram ->
            if (question.contains(ngram)) {
                logger.trace("Overlap: {}", ngram)
                ++count
            }
        }
        logger.debug("Answer ${answer.id} : {} out of {}", count, question.size())
        return answer.features.score = count / question.size()
    }

    protected List<Annotation> findAnnotations(Container container, String type) {
        View view = container.views.find { it.metadata.contains[type] }
        return view.annotations.findAll { it.atType == type }
    }
}
