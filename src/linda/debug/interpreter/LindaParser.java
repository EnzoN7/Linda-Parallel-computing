package linda.debug.interpreter;

import linda.Linda;
import linda.Tuple;
import linda.TupleFormatException;
import linda.debug.interpreter.commands.LindaBasicCommand;
import linda.debug.interpreter.commands.LindaEventRegisterCommand;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class LindaParser {
    protected String filePath;

    private FileReader fileReader;

    private BufferedReader bufferedReader;

    private Pattern basicCommandPattern;
    private Pattern eventRegisterCommandPattern;

    private Pattern debugCommandPattern;

    public LindaParser() {

        basicCommandPattern = Pattern.compile("(READ|TAKE|WRITE|READ_ALL|TAKE_ALL) (.+)");
        eventRegisterCommandPattern = Pattern.compile("(EVENT_REGISTER) (READ|TAKE) (IMMEDIATE|FUTURE) (.+) \"(.+)\"");
        debugCommandPattern = Pattern.compile("(DEBUG|MEMORY)");
    }

    public List<LindaCommand> parse() throws IOException {
        String _command = null;

        List<LindaCommand> commands = new ArrayList<>();

        while( (_command = bufferedReader.readLine()) != null) {
            commands.add( this.parseCommand(_command) );
        }

        return commands;
    }

    public abstract Optional<LindaCommand> next() throws Exception;

    protected LindaCommand parseCommand(String _command) {
        Matcher basicCommandMatcher = basicCommandPattern.matcher(_command);
        Matcher eventRegisterCommandMatcher = eventRegisterCommandPattern.matcher(_command);
        Matcher debugCommandMatcher = debugCommandPattern.matcher(_command);
        if(basicCommandMatcher.matches()) {
            String _operation = basicCommandMatcher.group(1);

            LindaOperation operation = this.parseOperation(_operation);

            if(this.isBasicOperation(operation)) {
                String _tuple = basicCommandMatcher.group(2);
                return new LindaBasicCommand(operation, this.parseTuple(_tuple));
            } else {
                throw new RuntimeException("Unhandled operation");
            }
        }
        else if(eventRegisterCommandMatcher.matches()) {
            String _operation = eventRegisterCommandMatcher.group(1);

            LindaOperation operation = this.parseOperation(_operation);
            if(this.isEventRegisterOperation(operation)) {
                String _mode = eventRegisterCommandMatcher.group(2);
                String _timing = eventRegisterCommandMatcher.group(3);
                String _template = eventRegisterCommandMatcher.group(4);
                String _callbackClassName = eventRegisterCommandMatcher.group(5);

                return new LindaEventRegisterCommand(operation, this.parseMode(_mode), this.parseTiming(_timing), this.parseTuple(_template), this.parseCallbackClassName(_callbackClassName));
            } else {
                throw new RuntimeException("Unhandled operation");
            }
        }
        else if(debugCommandMatcher.matches()) {
            String _operation = debugCommandMatcher.group(0);

            LindaOperation operation = this.parseOperation(_operation);

            return new LindaCommand(operation);
        }
        else {
            throw new RuntimeException("Error parsing command " + _command);
        }
    }

    protected String parseCallbackClassName(String callbackClassName) {
        return callbackClassName;
    }

    protected Linda.eventMode parseMode(String mode) {
        if(mode.equalsIgnoreCase("READ")) {
            return Linda.eventMode.READ;
        } else if(mode.equalsIgnoreCase("TAKE")) {
            return Linda.eventMode.TAKE;
        } else {
            throw new RuntimeException("Unhandled event mode " + mode);
        }
    }

    protected Linda.eventTiming parseTiming(String timing) {
        if(timing.equalsIgnoreCase("IMMEDIATE")) {
            return Linda.eventTiming.IMMEDIATE;
        } else if(timing.equalsIgnoreCase("FUTURE")) {
            return Linda.eventTiming.FUTURE;
        } else {
            throw new RuntimeException("Unhandled event timing " + timing);
        }
    }

    protected boolean isBasicOperation(LindaOperation operation) {
        return  operation == LindaOperation.READ
                || operation == LindaOperation.READ_ALL
                || operation == LindaOperation.TAKE
                || operation == LindaOperation.TAKE_ALL
                || operation == LindaOperation.WRITE;
    }

    protected boolean isEventRegisterOperation(LindaOperation operation) {
        return operation == LindaOperation.EVENT_REGISTER;
    }

    protected LindaOperation parseOperation(String _operation) {
        LindaOperation operation = LindaOperation.getFromString(_operation);

        if(operation == null)
            throw new RuntimeException("Error parsing operation " + _operation);

        return operation;
    }

    protected Tuple parseTuple(String _tuple) {
        try {
            return Tuple.valueOf(_tuple);
        } catch(TupleFormatException e) {
            throw e;
        }
    }
}
