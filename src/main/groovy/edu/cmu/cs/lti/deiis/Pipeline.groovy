package edu.cmu.cs.lti.deiis

import org.lappsgrid.api.WebService

/**
 * @author Keith Suderman
 */
class Pipeline {
    List<WebService> services = []

    void add(WebService service) {
        services << service
    }

    Pipeline leftShift(WebService service) {
        services << service
        return this
    }

    String execute(String input) {
        services.each { WebService service ->
            input = service.execute(input)
        }
        return input
    }
}
