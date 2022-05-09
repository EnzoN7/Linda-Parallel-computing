package linda.debug;

import linda.LindaObservable;
import linda.Tuple;
import linda.cache.LRULindaCache;
import linda.cache.LindaCacheObserver;
import linda.evaluator.LindaEvaluation;
import linda.evaluator.LindaEvaluator;
import linda.interpreter.LindaInterpreter;
import linda.interpreter.LindaInterpreterFactory;
import linda.server.LindaClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;

public class DebugLindaClient extends LindaClient implements DebugLinda, LindaObservable {

    protected String filePath;

    private LindaInterpreterFactory factory;

    private LindaInterpreter interpreter;

    private LindaCacheObserver cacheObserver;

    private LindaEvaluator evaluator;

    public static final int CacheCapacity = 10;

    public DebugLindaClient(String serverUri) {

        super(serverUri);

        factory = new LindaInterpreterFactory();
        cacheObserver = new LindaCacheObserver(new LRULindaCache(CacheCapacity), this);
        evaluator = new LindaEvaluator();
    }

    @Override
    public Tuple read(Tuple t) {
        UUID reqId = UUID.randomUUID();

        if(isEvaluated()) evaluator.onReadStart(reqId, t);

        if(isObservedByCacheObserver()) {
            Optional<Tuple> _tuple = cacheObserver.onReadStart(reqId, t);
            if (_tuple.isPresent()) {
                evaluator.setFromCache(reqId);

                Tuple result = _tuple.get();

                cacheObserver.onReadEnd(reqId, t, result);

                if(isEvaluated()) {
                    evaluator.onReadEnd(reqId, t, result);
                }

                System.out.println("Reading from cache..");
                return result;
            }
        }

        Tuple result = super.read(t);

        if(isObservedByCacheObserver()) cacheObserver.onReadEnd(reqId, t, result);
        if(isEvaluated()) evaluator.onReadEnd(reqId, t, result);

        return result;
    }

    @Override
    public Tuple take(Tuple t) {
        UUID reqId = UUID.randomUUID();

        //if(isObservedByCacheObserver()) cacheObserver.onTakeStart(reqId, t);
        Tuple result = super.take(t);
        if(isObservedByCacheObserver()) cacheObserver.onTakeEnd(reqId, t, result);

        return result;
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
                scanner.close();
                System.out.println("Linda Interpreter : END");
                return;
            }
        }

        scanner.close();
    }

    public void readUserCmds() {
        Scanner scanner = new Scanner(System.in).useDelimiter("\n");

        while(scanner.hasNext()) {
            String input = scanner.next();


            this.interpretText(input);

            this.next();
        }
    }


    public void runInterpreter(String[] args) {
        String type = args[0];

        // interprete file
        if(type.equals("-f")) {
            String filePath = args[1];

            this.interpretFile(filePath);

            // read all or one by one?
            this.printRunOptions();

            this.readUserInputs();
        }

        // interprete string
        else if(type.equals("-s")) {
            String text = args[1];

            this.interpretText(text);

            // read all or one by one?
            this.printRunOptions();

            this.readUserInputs();

        } else if (type.equals("-i")) {
            System.out.println("Enter a command..");

            this.readUserCmds();
        }
    }

    public static void main(String[] args) {
        DebugLindaClient client = new DebugLindaClient("rmi://localhost:7778/TestRMI");

        client.runInterpreter(args);
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

    @Override
    public Map<UUID, LindaEvaluation> getHistory() {
        try {
            return this.getDebugRemoteLinda().getHistory();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<UUID, LindaEvaluation> getLocalHistory() {
        return this.evaluator.getHistory();
    }

    @Override
    public void setEvaluator(LindaEvaluator observer) {
        this.evaluator = observer;
    }

    @Override
    public void unsetEvaluator() {
        this.evaluator = null;
    }

    @Override
    public boolean isEvaluated() {
        return this.evaluator != null;
    }

    @Override
    public void setCacheObserver(LindaCacheObserver observer) {
        this.cacheObserver = observer;
    }

    @Override
    public void unsetCacheObserver() {
        this.cacheObserver = null;
    }

    @Override
    public boolean isObservedByCacheObserver() {
        return this.cacheObserver != null;
    }

    public Map<Tuple, Deque<Tuple>> getCache() {
        return this.cacheObserver.getCache();
    }

    public void clearCache() {
        this.cacheObserver.clearCache();
    }
}
