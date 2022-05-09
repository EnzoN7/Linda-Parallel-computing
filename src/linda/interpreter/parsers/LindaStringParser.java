package linda.interpreter.parsers;

import linda.interpreter.LindaCommand;
import linda.interpreter.LindaParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class LindaStringParser extends LindaParser {

    private Scanner scanner;

    public LindaStringParser(String text) {
        super();

        scanner = new Scanner(text).useDelimiter("\n");
    }

    @Override
    public List<LindaCommand> parse() throws IOException {
        String _command;

        List<LindaCommand> commands = new ArrayList<>();

        while(scanner.hasNext()) {
            _command = scanner.next();
            commands.add( this.parseCommand(_command) );
        }

        return commands;
    }

    @Override
    public Optional<LindaCommand> next() throws Exception {

        if(!scanner.hasNext()) {
            scanner.close();
            return Optional.empty();
        }

        return Optional.ofNullable(this.parseCommand(scanner.next()));

    }
}
