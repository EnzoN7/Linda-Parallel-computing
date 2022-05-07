package linda.server.interpreter;

import linda.Tuple;
import linda.server.LindaClient;
import linda.server.interpreter.parsers.LindaFileParser;
import linda.server.interpreter.parsers.LindaStringParser;

import java.io.FileNotFoundException;

public class LindaInterpreterFactory {

    public LindaInterpreter createFileInterperer(LindaClient client, String filePath) throws FileNotFoundException {
        return new LindaInterpreter(new LindaFileParser(filePath), new LindaExecutor(client, true));
    }

    public LindaInterpreter createStringInterpreter(LindaClient client, String text) {
        return new LindaInterpreter(new LindaStringParser(text), new LindaExecutor(client, true));
    }

    public LindaCommand createReadCommand(Tuple template) {
        return new LindaCommand(LindaOperation.READ, template);
    }

    public LindaCommand createTakeCommand(Tuple template) {
        return new LindaCommand(LindaOperation.TAKE, template);
    }

    public LindaCommand createWriteCommand(Tuple tuple) {
        return new LindaCommand(LindaOperation.WRITE, tuple);
    }
}
