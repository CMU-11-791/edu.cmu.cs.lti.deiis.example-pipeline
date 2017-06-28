package edu.cmu.cs.lti.deiis

import org.junit.Test

/**
 * @author Keith Suderman
 */
class ResourceTest {

    @Test
    void testBoothTxt() {
        InputStream stream = Main.class.getResourceAsStream("/booth.txt")
        assert stream != null
    }
}
