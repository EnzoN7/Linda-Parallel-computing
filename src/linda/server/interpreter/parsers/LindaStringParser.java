package linda.server.parser;

import linda.server.file.LindaFileCommand;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;

public class LindaStringParser extends LindaParser {

    private Scanner scanner;

    public LindaStringParser(String text) {
        super();

        scanner = new Scanner(text);
    }

    @Override
    public Optional<LindaFileCommand> next() throws Exception {

        if(!scanner.hasNext()) {
            scanner.close();
            return Optional.empty();
        }

        return Optional.ofNullable(this.parseCommand(scanner.next()));

    }
}
