package linda.server.interpreter;

import linda.Tuple;
import linda.server.AdvancedLindaClient;
import linda.server.LindaClient;
import linda.server.interpreter.commands.LindaBasicCommand;
import linda.server.interpreter.commands.LindaEventRegisterCommand;

import java.util.Collection;
import java.util.List;

public class LindaExecutor {

    private AdvancedLindaClient client;

    private boolean trace;

    private boolean timeEval;

    public LindaExecutor(AdvancedLindaClient client, boolean trace, boolean timeEval) {
        this.client = client;
        this.trace = trace;
        this.timeEval = timeEval;
    }

    public void execute(LindaCommand command) {
        if(command instanceof LindaBasicCommand) {
            this.execute((LindaBasicCommand) command);
        } else if(command instanceof LindaEventRegisterCommand) {
            this.execute((LindaEventRegisterCommand) command);
        } else if(command.getOperation().equals(LindaOperation.DEBUG)){
            int availableProcessors = client.getAvailableProcessors();
            List<Tuple> tuples = client.getTuples();
            int tuplesNumber = client.getTuplesNumber();
            long tupleSpaceSize = client.getTupleSpaceSize();

            System.out.println("Available Processors : " + availableProcessors);
            System.out.println("Tuples : " + tuples);
            System.out.println("Tuples number : " + tuplesNumber);
            System.out.println("TupleSpace size : " + tupleSpaceSize);

        } else if(command.getOperation().equals(LindaOperation.MEMORY)){
            long freeMemory = client.getFreeMemory();
            long inUseMemory = client.getInUseMemory();
            long totalMemory = client.getTotalMemory();
            long maxMemory = client.getMaxMemory();

            System.out.println("Free Memory : " + freeMemory);
            System.out.println("In Use Memory : " + inUseMemory);
            System.out.println("Total Memory : " + totalMemory);
            System.out.println("Max Memory : " + maxMemory);

        } else {
            throw new RuntimeException("Unhandled command " + command.getClass());
        }
    }

    public void execute(LindaBasicCommand command) {

        long startTime = 0;
        long endTime = 0;
        long duration = 0;

        if(command.is(LindaOperation.READ) ) {

            if(trace) System.out.println("Run : " + command);

            if(timeEval) {
                startTime = System.nanoTime();
            }

            Tuple result = client.read(command.getTuple());

            if(timeEval)  {
                endTime = System.nanoTime();

                duration = (endTime - startTime) / 1000000 ;

                System.out.println("Duration : " + duration + "ms");
            }

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

    public void execute(List<LindaCommand> commands) {
        for(LindaCommand command : commands) {
            if(command instanceof LindaBasicCommand)
                this.execute((LindaBasicCommand) command);
            else if(command instanceof LindaEventRegisterCommand)
                this.execute((LindaEventRegisterCommand) command);
            else
                throw new RuntimeException("Unhandled LindaCommand type " + command.getClass());
        }
    }
}