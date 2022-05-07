package linda.server.interpreter;

import linda.Tuple;
import linda.server.LindaClient;
import linda.server.interpreter.commands.LindaBasicCommand;

import java.util.List;

public class LindaExecutor {

    private LindaClient client;

    private boolean trace;

    public LindaExecutor(LindaClient client, boolean trace) {
        this.client = client;
        this.trace = trace;
    }

    public void execute(LindaBasicCommand command) {

        if(command.is(LindaOperation.READ) ) {
            if(trace) System.out.println("Run : " + command);

            Tuple result = client.read(command.getTuple());

            if(trace) System.out.println("Result : " + result);
        } else if(command.is(LindaOperation.TAKE) ) {
            if(trace) System.out.println("Run : " + command);

            Tuple result = client.take(command.getTuple());

            if(trace) System.out.println("Result of " + command + " : " + result);
        } else if(command.is(LindaOperation.WRITE) ) {
            if(trace) System.out.println("Starts writing " + command);
            client.write(command.getTuple());
            if(trace) System.out.println("End writing " + command);

        } else {
            throw new RuntimeException("Incorrect operation");
        }
    }

    public void execute(List<LindaBasicCommand> commands) {
        for(LindaBasicCommand command : commands) {
            this.execute(command);
        }
    }
}
