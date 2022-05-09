package linda.interpreter;

import linda.Linda;
import linda.Tuple;
import linda.TupleFormatException;
import linda.interpreter.commands.*;
import linda.interpreter.commands.parameters.LindaCacheOperation;
import linda.interpreter.commands.parameters.LindaHistoryType;
import linda.interpreter.commands.parameters.LindaStatsOperation;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class LindaParser {

    private Pattern basicCommandPattern;
    private Pattern eventRegisterCommandPattern;
    private Pattern historyCommandPattern;
    private Pattern debugCommandPattern;
    private Pattern cacheCommandPattern;
    private Pattern statsCommandPattern;

    public LindaParser() {

        basicCommandPattern = Pattern.compile("(READ|TAKE|WRITE|READ_ALL|TAKE_ALL) (.+)", Pattern.CASE_INSENSITIVE);
        eventRegisterCommandPattern = Pattern.compile("(EVENT_REGISTER) (READ|TAKE) (IMMEDIATE|FUTURE) (.+) \"(.+)\"", Pattern.CASE_INSENSITIVE);
        debugCommandPattern = Pattern.compile("(SERVER|MEMORY)", Pattern.CASE_INSENSITIVE);
        historyCommandPattern = Pattern.compile("(HISTORY) (LOCAL|REMOTE)", Pattern.CASE_INSENSITIVE);
        cacheCommandPattern = Pattern.compile("(CACHE) (PRINT|CLEAR|SIZE)", Pattern.CASE_INSENSITIVE);
        statsCommandPattern = Pattern.compile("(STATS) (CACHE)", Pattern.CASE_INSENSITIVE);
    }

    public abstract  List<LindaCommand> parse() throws  IOException;

    public abstract Optional<LindaCommand> next() throws Exception;

    protected LindaCommand parseCommand(String _command) {
        Matcher basicCommandMatcher = basicCommandPattern.matcher(_command);
        Matcher eventRegisterCommandMatcher = eventRegisterCommandPattern.matcher(_command);
        Matcher debugCommandMatcher = debugCommandPattern.matcher(_command);
        Matcher historyCommandMatcher = historyCommandPattern.matcher(_command);
        Matcher cacheCommandMatcher = cacheCommandPattern.matcher(_command);
        Matcher statsCommandMatcher = statsCommandPattern.matcher(_command);

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
        else if(historyCommandMatcher.matches()) {
            String _operation = historyCommandMatcher.group(1);
            String _which = historyCommandMatcher.group(2);

            LindaOperation operation = this.parseOperation(_operation);
            LindaHistoryType type = this.parseHistoryType(_which);

            return new LindaHistoryCommand(operation, type);
        }
        else if(cacheCommandMatcher.matches()) {
            String _operation = cacheCommandMatcher.group(1);
            String _what = cacheCommandMatcher.group(2);

            LindaOperation operation = this.parseOperation(_operation);
            LindaCacheOperation subOperation = this.parseCacheOp(_what);

            return new LindaCacheCommand(operation, subOperation);
        }
        else if(statsCommandMatcher.matches()) {
            String _operation = statsCommandMatcher.group(1);
            String _what = statsCommandMatcher.group(2);

            LindaOperation operation = this.parseOperation(_operation);
            LindaStatsOperation subOperation = this.parseStatsOp(_what);

            return new LindaStatsCommand(operation, subOperation);
        }
        else {
            throw new RuntimeException("Error parsing command " + _command + ", instruction mismatch.");
        }
    }

    protected LindaStatsOperation parseStatsOp(String what) {
        if(what.equalsIgnoreCase("CACHE")) {
            return LindaStatsOperation.CACHE;
        } else {
            throw new RuntimeException("Unhandled stats type : "  + what);
        }
    }

    protected LindaCacheOperation parseCacheOp(String what) {
        if(what.equalsIgnoreCase("PRINT")) {
            return LindaCacheOperation.PRINT;
        } else if(what.equalsIgnoreCase("CLEAR")) {
            return LindaCacheOperation.CLEAR;        }
        else if(what.equalsIgnoreCase("SIZE")) {
            return LindaCacheOperation.SIZE;
        } else {
            throw new RuntimeException("Unhandled cache type : "  + what);
        }
    }

    protected LindaHistoryType parseHistoryType(String which) {
        if(which.equalsIgnoreCase("LOCAL")) {
            return LindaHistoryType.LOCAL;
        } else if(which.equalsIgnoreCase("REMOTE")) {
            return LindaHistoryType.REMOTE;
        } else {
            throw new RuntimeException("Unhandled history type : "  + which);
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
