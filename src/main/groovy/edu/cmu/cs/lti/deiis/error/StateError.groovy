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

package edu.cmu.cs.lti.deiis.error

class StateError extends Exception {
    StateError() {
    }

    StateError(String var1) {
        super(var1)
    }

    StateError(String var1, Throwable var2) {
        super(var1, var2)
    }

    StateError(Throwable var1) {
        super(var1)
    }

    StateError(String var1, Throwable var2, boolean var3, boolean var4) {
        super(var1, var2, var3, var4)
    }
}
