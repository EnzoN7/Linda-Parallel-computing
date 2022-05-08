package linda.debug.interpreter.parsers;

import linda.debug.interpreter.LindaCommand;
import linda.debug.interpreter.LindaParser;

import java.io.*;
import java.util.Optional;

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
    public Optional<LindaCommand> next() throws Exception {
        String _command = bufferedReader.readLine();

        if(_command == null)
            return Optional.empty();

        return Optional.of( this.parseCommand(_command) );
    }
}
