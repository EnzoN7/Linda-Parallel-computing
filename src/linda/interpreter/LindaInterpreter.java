package linda.interpreter;

import linda.evaluator.LindaEvaluator;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class LindaInterpreter {

    private LindaParser parser;

    private LindaExecutor executor;

    private LindaEvaluator evaluator;

    public LindaInterpreter(LindaParser parser, LindaExecutor executor, LindaEvaluator evaluator) {
        this.parser = parser;
        this.executor = executor;
        this.evaluator = evaluator;

    }

    public void next() throws Exception {
        Optional<LindaCommand> _cmd = this.parser.next();

        if(_cmd.isPresent()) {
            LindaCommand cmd = _cmd.get();
            this.executor.execute(cmd);
        } else {
            throw new RuntimeException("Cannot parse more");
        }
    }

    public void runAll() throws IOException {
        List<LindaCommand> commands = this.parser.parse();

        this.executor.execute(commands);
    }

}
