package edu.cmu.cs.lti.deiis.scoring

import edu.cmu.cs.lti.deiis.annotators.AbstractAnnotator
import edu.cmu.cs.lti.deiis.model.Types
import groovy.util.logging.Slf4j
import org.lappsgrid.discriminator.Discriminators
import org.lappsgrid.metadata.ServiceMetadataBuilder
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.Serializer

/**
 * @author Keith Suderman
 */
@Slf4j("logger")
class Evaluator extends AbstractAnnotator {

    int n

    public Evaluator() {
        super(Evaluator.class)
        n = 3
    }

    public Evaluator(int n) {
        super(Evaluator.class)
        this.n = n
    }

    protected ServiceMetadataBuilder configure(ServiceMetadataBuilder builder) {
        builder.name(this.class.name)
                .requireFormat(Discriminators.Uri.LIF)
                .produceFormat(Discriminators.Uri.CSV)
        return builder
    }

    String execute(String input) {
        Data data = Serializer.parse(input, Data)
        if (Types.ERROR == data.discriminator) {
            return input
        }
        if (Types.LAPPS != data.discriminator ) {
            return unsupported(data.discriminator)
        }
        if (data.parameters?.n) {
            n = data.parameters.n as int
        }
        logger.info("Ranking N={}", n)
        String s = data.payload.toString()
        int correct = 0
        List<String> lines = s.readLines()
        lines[0..(n-1)].each { String line ->
            if (line.startsWith('1')) {
                ++correct
            }
        }
        int missed = 0
        lines[n..-1].each { String line ->
            if (line.startsWith('1')) {
                ++missed
            }
        }
        float p = correct / n
        float r = correct / (correct + missed)
        float f = 2 * (p * r) / (p + r)
//        StringBuilder buffer = new StringBuilder()
//        buffer.append("Precision@$n\np: ")
//        buffer.append(p)
//        buffer.append '\nr: '
//        buffer.append(r)
//        buffer.append '\nf: '
//        buffer.append ()
//        buffer.append '\n'


//        data.discriminator = Types.PRF
//        data.payload = String.format("Precision @ %d\np %1.3f\nr %1.3f\nf %1.3f", n, p, r, f)
//        return data.asJson()
        return String.format("%d,%1.3f,%1.3f,%1.3f", n, p, r, f)
    }
}
