package linda.interpreter;

import linda.Tuple;
import linda.evaluator.LindaEvaluation;
import linda.interpreter.commands.*;
import linda.interpreter.commands.parameters.LindaCacheOperation;
import linda.debug.DebugLindaClient;
import linda.interpreter.commands.parameters.LindaStatsOperation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class LindaExecutor {

    private DebugLindaClient client;

    private boolean trace;

    private boolean timeEval;

    public LindaExecutor(DebugLindaClient client, boolean trace, boolean timeEval) {
        this.client = client;
        this.trace = trace;
        this.timeEval = timeEval;
    }

    public void execute(LindaCommand command) {
        if(command instanceof LindaBasicCommand) {
            this.execute((LindaBasicCommand) command);
        } else if(command instanceof LindaEventRegisterCommand) {
            this.execute((LindaEventRegisterCommand) command);
        } else if (command instanceof LindaHistoryCommand) {
            this.execute((LindaHistoryCommand) command);
        } else if (command instanceof LindaCacheCommand) {
            this.execute((LindaCacheCommand) command);
        } else if (command instanceof LindaStatsCommand) {
            this.execute((LindaStatsCommand) command);
        } else if(command.getOperation().equals(LindaOperation.SERVER)){
            runServerInfo();
        } else if(command.getOperation().equals(LindaOperation.MEMORY)){
            runMemory();
        } else {
            throw new RuntimeException("Unhandled command " + command.getClass());
        }
    }

    private void runClearCache() {
        client.clearCache();

        System.out.println("Cache cleared.");
    }

    private void runMemory() {
        long freeMemory = client.getFreeMemory();
        long inUseMemory = client.getInUseMemory();
        long totalMemory = client.getTotalMemory();
        long maxMemory = client.getMaxMemory();

        System.out.println("Free Memory : " + freeMemory);
        System.out.println("In Use Memory : " + inUseMemory);
        System.out.println("Total Memory : " + totalMemory);
        System.out.println("Max Memory : " + maxMemory);
    }

    private void runCache() {
        System.out.print("Cache : " + client.getCache());
        System.out.println();
    }
    private void runServerInfo() {
        int availableProcessors = client.getAvailableProcessors();
        List<Tuple> tuples = client.getTuples();
        int tuplesNumber = client.getTuplesNumber();
        long tupleSpaceSize = client.getTupleSpaceSize();

        System.out.println("Available Processors : " + availableProcessors);
        System.out.println("Tuples : " + tuples);
        System.out.println("Tuples number : " + tuplesNumber);
        System.out.println("TupleSpace size : " + tupleSpaceSize);

    }

    private void runRemoteHistory() {
        var history = client.getHistory();

        if(history.isEmpty()) {
            System.out.println("History is empty.");
        }

        for(var element : history.entrySet()) {
            System.out.println(element.getValue().operation + " " + element.getValue().tuple);
        }
    }

    private void runLocalHistory() {
        var history = client.getLocalHistory();

        if(history.isEmpty()) {
            System.out.println("History is empty.");
        }

        for(var element : history.entrySet()) {
            String fromCache = element.getValue().fromCache ? " (from cache)" : "";
            System.out.println(element.getValue().operation + " " + element.getValue().tuple + " : " + element.getValue().duration + fromCache);
        }
    }

    private void runStats() {

    }

    public void execute(LindaCacheCommand command) {
        if(command.getSubOperation().equals(LindaCacheOperation.PRINT)) {
            runCache();
        } else if(command.getSubOperation().equals(LindaCacheOperation.CLEAR)) {
            runClearCache();
        } else if(command.getSubOperation().equals(LindaCacheOperation.SIZE)) {

        } else {
            throw new RuntimeException("Incorrect operation, must be either PRINT, CLEAR or SIZE");
        }
    }
    public void execute(LindaHistoryCommand command) {
        if(command.isLocal()) {
            runLocalHistory();
        } else if(command.isRemote()) {
            runRemoteHistory();
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

    public void execute(LindaStatsCommand command) {
        if(command.is(LindaOperation.STATS)) {
            if(command.getSubOperation().equals(LindaStatsOperation.CACHE)) {
                runCompareCache();
            }
        } else {
            throw new RuntimeException("Incorrect operation, must be STATS");
        }
    }

    private void runCompareCache() {
        var evaluations = client.getLocalHistory();

        long cacheTime = 0;
        long noCacheTime = 0;

        try {
            BufferedWriter bufferedWriter = new BufferedWriter ( new FileWriter("./scripts/cache-time-eval.csv", true));

            for(LindaEvaluation evaluation : evaluations.values()) {
                if(evaluation.fromCache) {
                    cacheTime += evaluation.duration;
                } else {
                    noCacheTime += evaluation.duration;
                }

                this.appendCacheStats(bufferedWriter, evaluation.duration, evaluation.fromCache);
            }

            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int n = evaluations.values().size();

        long meanCacheTime = cacheTime / n;
        long meanNoCacheTime = noCacheTime / n;

        System.out.println("Moyenne du temps d'execution sans cache : " + meanNoCacheTime + "ns, soit " + ((float)meanNoCacheTime/1000000) + "ms.");
        System.out.println("Moyenne du temps d'execution avec cache : " + meanCacheTime + "ns, soit " + ((float)meanCacheTime/1000000) + "ms.");

        try {
            appendCacheStatsMean(meanCacheTime, meanNoCacheTime);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void appendCacheStats(BufferedWriter bufferedWriter, long duration, boolean fromCache) throws IOException {
        bufferedWriter.append(duration + ";" + (fromCache ? 1 : 0) + "\n");
    }

    private void appendCacheStatsMean(long meanCacheTime, long meanNoCacheTime) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter (new FileWriter("./scripts/cache-time-eval-means.csv", true));
        bufferedWriter.append(meanCacheTime + ";" + meanNoCacheTime + "\n");
        bufferedWriter.close();
    }

    public void execute(List<LindaCommand> commands) {
        for(LindaCommand command : commands) {
            if(command instanceof LindaBasicCommand)
                this.execute((LindaBasicCommand) command);
            else if(command instanceof LindaEventRegisterCommand)
                this.execute((LindaEventRegisterCommand) command);
            else if(command instanceof LindaCacheCommand)
                this.execute((LindaCacheCommand) command);
            else if(command instanceof LindaHistoryCommand)
                this.execute((LindaHistoryCommand) command);
            else if(command instanceof LindaStatsCommand)
                this.execute((LindaStatsCommand) command);
            else
                throw new RuntimeException("Unhandled LindaCommand type " + command.getClass());
        }
    }

    public DebugLindaClient getClient() {
        return client;
    }
}
