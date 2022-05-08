package linda.debug;

import linda.Tuple;
import linda.debug.evaluator.LindaEvaluation;
import linda.debug.evaluator.LindaEvaluator;
import linda.server.LindaServer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DebugLindaServer extends LindaServer implements DebugRemoteLinda {

    private LindaEvaluator evaluator;

    public DebugLindaServer() throws RemoteException {
        this.kernel = new DebugCentralizedLinda();
        this.evaluator = new LindaEvaluator();

        this.getDebugKernel().setObserver(this.evaluator);
    }

    protected DebugCentralizedLinda getDebugKernel() {
        return (DebugCentralizedLinda)kernel;
    }

    @Override
    public List<Tuple> getTuples() throws RemoteException {
        return this.getDebugKernel().getTuples();
    }

    @Override
    public long getTupleSpaceSize() throws RemoteException {
        return this.getDebugKernel().getTupleSpaceSize();
    }

    @Override
    public int getTuplesNumber() throws RemoteException {
        return this.getDebugKernel().getTuplesNumber();
    }

    @Override
    public long getFreeMemory() throws RemoteException {
        return this.getDebugKernel().getFreeMemory();
    }

    @Override
    public long getTotalMemory() throws RemoteException {
        return this.getDebugKernel().getTotalMemory();
    }

    @Override
    public long getInUseMemory() throws RemoteException {
        return this.getDebugKernel().getInUseMemory();
    }

    @Override
    public long getMaxMemory() throws RemoteException {
        return this.getDebugKernel().getMaxMemory();
    }

    @Override
    public int getAvailableProcessors() throws RemoteException {
        return this.getDebugKernel().getAvailableProcessors();
    }

    @Override
    public Map<UUID, LindaEvaluation> getHistory() throws RemoteException {
        return this.evaluator.getHistory();
    }

    public static void main(String[] args) {
        try {

            var registry = LocateRegistry.createRegistry(7774);

            LindaServer server = new DebugLindaServer();


            String url = "rmi://" + "localhost:7778" + "/TestRMI";
            System.out.println("Enregistrement de l'objet avec l'url : " + url);
            registry.rebind(url, server);

            System.out.println("Serveur lanc�");
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
