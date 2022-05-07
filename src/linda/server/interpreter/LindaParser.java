package linda.server.parser;

import linda.Tuple;
import linda.TupleFormatException;
import linda.server.file.LindaFileCommand;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
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

    private Pattern pattern;

    public LindaParser() {
        pattern = Pattern.compile("(READ|TAKE|WRITE) (.+)");
    }

    public List<LindaFileCommand> parse() throws IOException {
        String _command = null;

        List<LindaFileCommand> commands = new ArrayList<>();

        while( (_command = bufferedReader.readLine()) != null) {
            commands.add( this.parseCommand(_command) );
        }

        return commands;
    }

    public abstract Optional<LindaFileCommand> next() throws Exception;

    protected LindaFileCommand parseCommand(String _command) {
        Matcher matcher = pattern.matcher(_command);
        if(matcher.matches()) {
            String _operation = matcher.group(1);
            String _tuple = matcher.group(2);

            return new LindaFileCommand(this.parseOperation(_operation), this.parseTuple(_tuple));
        } else {
            throw new RuntimeException("Error parsing command " + _command);
        }
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
