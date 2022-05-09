package linda.interpreter.parsers;

import linda.interpreter.LindaCommand;
import linda.interpreter.LindaParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
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
    public List<LindaCommand> parse() throws IOException {
        String _command;

        List<LindaCommand> commands = new ArrayList<>();

        while( (_command = bufferedReader.readLine()) != null) {
            commands.add( this.parseCommand(_command) );
        }

        return commands;
    }

    @Override
    public Optional<LindaCommand> next() throws Exception {
        String _command = bufferedReader.readLine();

        if(_command == null)
            return Optional.empty();

        return Optional.of( this.parseCommand(_command) );
    }
}
