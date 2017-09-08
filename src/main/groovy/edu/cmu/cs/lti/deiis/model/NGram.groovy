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

import org.lappsgrid.serialization.lif.Annotation

class NGram {
    static enum Type {
        UNIGRAM(1), BIGRAM(2), TRIGRAM(3);

        private int n;
        Type(int n) {
            this.n = n
        }
        int n() { return n }
        static Type valueOf(int i) {
            if (i == 1) return UNIGRAM
            if (i == 2) return BIGRAM
            return TRIGRAM
        }
    }

    List<Annotation> grams = []
    Type type

    NGram(int size) {
        this.type = Type.valueOf(size)
    }
    
    NGram(Type type) {
        this.type = type
    }

    int size() {
        return grams.size()
    }

    int n() {
        return type.n()
    }

    NGram add(Annotation item) {
        if (grams.size() >= type.n()) {
            grams.remove(0)
        }
        grams.add(item)
        return this
    }

    NGram leftShift(Annotation item) {
        return add(item)
    }

    String toString() {
        grams.collect{ it.features.string }.join(' ')
    }

    Annotation annotate(String id) {
        int start = grams[0].start
        int end = grams[-1].end
        Annotation a = new Annotation(id, Types.NGRAM, start, end)
        a.features.type = type
        a.features.text = this.toString()
        a.label = type.toString()
        return a
    }

    boolean equals(Object object) {
        if (!(object instanceof NGram)) {
            return false
        }
        NGram other = (NGram) object
        return this.type.n == other.type.n && this.toString() == other.toString()
    }

    int hashCode() {
        return this.toString().hashCode()
    }
}
