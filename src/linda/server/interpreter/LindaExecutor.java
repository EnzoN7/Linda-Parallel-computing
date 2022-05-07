package linda.server.interpreter;

import linda.Tuple;
import linda.server.LindaClient;
import linda.server.interpreter.commands.LindaAllCommand;
import linda.server.interpreter.commands.LindaBasicCommand;
import linda.server.interpreter.commands.LindaEventRegisterCommand;

import java.util.Collection;
import java.util.List;

public class LindaExecutor {

    private LindaClient client;

    private boolean trace;

    public LindaExecutor(LindaClient client, boolean trace) {
        this.client = client;
        this.trace = trace;
    }

    public void execute(LindaCommand command) {
        if(command instanceof LindaBasicCommand) {
            this.execute((LindaBasicCommand) command);
        } else if(command instanceof LindaEventRegisterCommand) {
            this.execute((LindaEventRegisterCommand) command);
        } else if(command instanceof  LindaAllCommand) {
            this.execute((LindaAllCommand) command);
        }
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
            if (trace) System.out.println("Starts writing " + command);
            client.write(command.getTuple());
            if (trace) System.out.println("End writing " + command);
        } else if(command.is(LindaOperation.READ_ALL) ) {
            if(trace) System.out.println("Run : " + command);

            Collection<Tuple> result = client.readAll(command.getTuple());

            if(trace) System.out.println("Result : " + result);
        } else if(command.is(LindaOperation.TAKE_ALL) ) {
            if(trace) System.out.println("Run : " + command);

            Collection<Tuple> result = client.takeAll(command.getTuple());

            if(trace) System.out.println("Result : " + result);
        } else {
            throw new RuntimeException("Incorrect operation, must be either READ, TAKE or WRITE");
        }
    }

    public void execute(LindaEventRegisterCommand command) {
        if(command.is(LindaOperation.EVENT_REGISTER)) {
            if(trace) System.out.println("Starts registring event " + command.getCallbackClass().getName());
            client.eventRegister(command.getMode(), command.getTiming(), command.getTemplate(), command.createCallbackInstance());
            if(trace) System.out.println("End registring event " + command.getCallbackClass().getName());
        } else {
            throw new RuntimeException("Incorrect operation, must be EVENT_REGISTER");
        }
    }

    public void execute(LindaAllCommand command) {
        throw new RuntimeException("LindaAllCommand not yet implemented.");
    }

    public void execute(List<LindaCommand> commands) {
        for(LindaCommand command : commands) {
            if(command instanceof LindaBasicCommand)
                this.execute((LindaBasicCommand) command);
            else if(command instanceof LindaEventRegisterCommand)
                this.execute((LindaEventRegisterCommand) command);
            else if(command instanceof LindaAllCommand)
                this.execute((LindaAllCommand) command);
            else
                throw new RuntimeException("Unhandled LindaCommand type " + command.getClass());
        }
    }
}