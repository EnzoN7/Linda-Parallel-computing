package linda.server;

import linda.server.interpreter.LindaInterpreter;
import linda.server.interpreter.LindaInterpreterFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvancedLindaClient extends LindaClient {

    protected String filePath;

    private LindaInterpreterFactory factory;

    private LindaInterpreter interpreter;

    public AdvancedLindaClient(String serverUri) {

        super(serverUri);

        factory = new LindaInterpreterFactory();

    }

    protected void interpretFile(String filePath) {
        try {
            this.interpreter = factory.createFileInterperer(this, filePath);


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected void interpretText(String text) {
        this.interpreter = factory.createStringInterpreter(this, text);
    }

    protected void next() {
        try {
            this.interpreter.next();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void runAll() {
        try {
            this.interpreter.runAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void printRunOptions() {
        System.out.println("To run one by one, click '.' ");
        System.out.println("To run all at once, click ';' ");
        System.out.println("To stop, click '?' ");
    }

    public void readUserInputs() {
        Scanner scanner = new Scanner(System.in);

        while(scanner.hasNext()) {
            String input = scanner.next();

            if(input.equals(".")) {
                this.next();
            } else if(input.equals(";")) {
                this.runAll();
            } else if(input.equals("?")) {
                System.out.println("Linda Interpreter : END");
                return;
            }
        }
    }

    public void readUserCmds() {
        Scanner scanner = new Scanner(System.in).useDelimiter("\n");

        while(scanner.hasNext()) {
            String input = scanner.next();


            this.interpretText(input);

            this.next();
        }
    }

    public static void main(String[] args) {

        AdvancedLindaClient client = new AdvancedLindaClient("rmi://localhost:7778/TestRMI");

        String type = args[0];

        // interprete file
        if(type.equals("-f")) {
            String filePath = args[1];

            client.interpretFile(filePath);

            // read all or one by one?
            client.printRunOptions();

            client.readUserInputs();
        }

        // interprete string
        else if(type.equals("-s")) {
            String text = args[1];

            client.interpretText(text);

            // read all or one by one?
            client.printRunOptions();

            client.readUserInputs();

        } else if (type.equals("-i")) {
            System.out.println("Enter a command..");

            client.readUserCmds();
        }
    }
}
