package linda.server;

import linda.Tuple;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Manager  {

    private int partitions = 10; // Default value of partitions is set to "10"
    private GateKeeper gateKeeper;
    Registry registry;

    public Manager() {
        initiateGateKeeper(this.partitions);
    }

    public Manager(int partitions) {
        this.partitions = partitions;
        initiateGateKeeper(this.partitions);
    }

    private void initiateGateKeeper(int partitions) {
        try {
            this.registry = LocateRegistry.createRegistry(7774);
            this.gateKeeper = new GateKeeper(partitions, registry);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void startServers() {
        for (int i = 1; i <= partitions ; i++) {
            int finalI = i;
            GateKeeper finalGateKeeper = this.gateKeeper;
            Manager finalManager = this;
            (new Thread() {
                @Override
                public void run() {
                    try {
                        String url = "rmi://" + "localhost:7778" + "/Linda/server" + finalGateKeeper.hashCode() + "" +finalI;

                        registry.rebind(url, new LindaServer(finalI, finalManager));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public Object redistribute(int callerAdd, String methodName,Object ...args) throws RemoteException {

        Object result = LocateRegistry.createRegistry(7774);

        for (int i = 1; i <= partitions; i++) {
            if (i == callerAdd) {
                continue;
            }

            String url = "rmi://" + "localhost:7778" + "/Linda/server" + this.gateKeeper.hashCode() + "" + i;

            try {
                RemoteLinda server = (RemoteLinda) registry.lookup(url);

                result = server.getClass().getMethod(methodName).invoke(args);

                if (result != null) {
                    break;
                }
            } catch (NotBoundException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public void startUp() {
        try {
            String url = "rmi://" + "localhost:7778" + "/Linda";

            registry.rebind(url, this.gateKeeper);

            startServers();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
