package linda.server.interpreter.parsers;

import linda.server.interpreter.LindaCommand;
import linda.server.interpreter.LindaParser;

import java.util.Optional;
import java.util.Scanner;

public class LindaStringParser extends LindaParser {

    private Scanner scanner;

    public LindaStringParser(String text) {
        super();

        scanner = new Scanner(text).useDelimiter("\n");
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