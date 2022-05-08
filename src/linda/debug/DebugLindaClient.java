package linda.debug;

import linda.Tuple;
import linda.debug.interpreter.LindaInterpreter;
import linda.debug.interpreter.LindaInterpreterFactory;
import linda.server.LindaClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

public class DebugLindaClient extends LindaClient implements DebugLinda {

    protected String filePath;

    private LindaInterpreterFactory factory;

    private LindaInterpreter interpreter;

    public DebugLindaClient(String serverUri) {

        super(serverUri);

        factory = new LindaInterpreterFactory();

    }

    protected void interpretFile(String filePath) {
        try {
            this.interpreter = factory.createFileInterpreter(this, filePath);


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

        DebugLindaClient client = new DebugLindaClient("rmi://localhost:7778/TestRMI");

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

    protected DebugRemoteLinda getDebugRemoteLinda() {
        return (DebugRemoteLinda)remoteLinda;
    }

    @Override
    public List<Tuple> getTuples() {
        try {
            return this.getDebugRemoteLinda().getTuples();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getTupleSpaceSize() {
        try {
            return this.getDebugRemoteLinda().getTupleSpaceSize();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getTuplesNumber() {
        try {
            return this.getDebugRemoteLinda().getTuplesNumber();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getFreeMemory() {
        try {
            return this.getDebugRemoteLinda().getFreeMemory();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getTotalMemory() {
        try {
            return this.getDebugRemoteLinda().getTotalMemory();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getInUseMemory() {
        try {
            return this.getDebugRemoteLinda().getInUseMemory();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getMaxMemory() {
        try {
            return this.getDebugRemoteLinda().getMaxMemory();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getAvailableProcessors() {
        try {
            return this.getDebugRemoteLinda().getAvailableProcessors();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
