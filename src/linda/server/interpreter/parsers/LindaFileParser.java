package linda.server.file;

import linda.Tuple;
import linda.TupleFormatException;
import linda.server.parser.LindaOperation;
import linda.server.parser.LindaParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LindaFileParser extends LindaParser {

    protected String filePath;

    private FileReader fileReader;

    private BufferedReader bufferedReader;

    public LindaFileParser(String filePath) throws FileNotFoundException {
        super();

        this.filePath = filePath;

        fileReader = new FileReader(filePath);
        bufferedReader = new BufferedReader(fileReader);
    }

    @Override
    public Optional<LindaFileCommand> next() throws Exception {
        String _command = bufferedReader.readLine();

        if(_command == null)
            return Optional.empty();

        return Optional.of( this.parseCommand(_command) );
    }
}
