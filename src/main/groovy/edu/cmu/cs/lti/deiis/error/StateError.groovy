package edu.cmu.cs.lti.deiis.error

/**
 * @author Keith Suderman
 */
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
