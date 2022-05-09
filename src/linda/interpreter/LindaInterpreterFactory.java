package linda.interpreter;

import linda.Linda;
import linda.Tuple;
import linda.debug.DebugLindaClient;
import linda.evaluator.LindaEvaluator;
import linda.interpreter.commands.LindaBasicCommand;
import linda.interpreter.commands.LindaEventRegisterCommand;
import linda.interpreter.parsers.LindaFileParser;
import linda.interpreter.parsers.LindaStringParser;

import java.io.FileNotFoundException;

public class LindaInterpreterFactory {

    public LindaInterpreter createFileInterpreter(DebugLindaClient client, String filePath) throws FileNotFoundException {
        return new LindaInterpreter(new LindaFileParser(filePath), new LindaExecutor(client, true, true), new LindaEvaluator());
    }

    public LindaInterpreter createStringInterpreter(DebugLindaClient client, String text) {
        return new LindaInterpreter(new LindaStringParser(text), new LindaExecutor(client, true, true), new LindaEvaluator());
    }

    public LindaBasicCommand createReadCommand(Tuple template) {
        return new LindaBasicCommand(LindaOperation.READ, template);
    }

    public LindaBasicCommand createTakeCommand(Tuple template) {
        return new LindaBasicCommand(LindaOperation.TAKE, template);
    }

    public LindaBasicCommand createWriteCommand(Tuple tuple) {
        return new LindaBasicCommand(LindaOperation.WRITE, tuple);
    }

    public LindaEventRegisterCommand createReadEventCommand(Linda.eventTiming timing, Tuple template, String callbackClassName) {
        return new LindaEventRegisterCommand(LindaOperation.EVENT_REGISTER, Linda.eventMode.READ, timing, template, callbackClassName );
    }

    public LindaEventRegisterCommand createReadImmediateEventCommand(Tuple template, String callbackClassName) {
        return new LindaEventRegisterCommand(LindaOperation.EVENT_REGISTER, Linda.eventMode.READ, Linda.eventTiming.IMMEDIATE, template, callbackClassName );
    }

    public LindaEventRegisterCommand createReadFutureEventCommand(Tuple template, String callbackClassName) {
        return new LindaEventRegisterCommand(LindaOperation.EVENT_REGISTER, Linda.eventMode.READ, Linda.eventTiming.FUTURE, template, callbackClassName );
    }
    public LindaEventRegisterCommand createTakeEventCommand(Linda.eventTiming timing, Tuple template, String callbackClassName) {
        return new LindaEventRegisterCommand(LindaOperation.EVENT_REGISTER, Linda.eventMode.TAKE, timing, template, callbackClassName );
    }

    public LindaEventRegisterCommand createTakeImmediateEventCommand(Tuple template, String callbackClassName) {
        return new LindaEventRegisterCommand(LindaOperation.EVENT_REGISTER, Linda.eventMode.TAKE, Linda.eventTiming.IMMEDIATE, template, callbackClassName );
    }

    public LindaEventRegisterCommand createTakeFutureEventCommand(Tuple template, String callbackClassName) {
        return new LindaEventRegisterCommand(LindaOperation.EVENT_REGISTER, Linda.eventMode.TAKE, Linda.eventTiming.FUTURE, template, callbackClassName );
    }

}
