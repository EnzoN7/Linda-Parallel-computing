package linda.server;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class GateKeeper extends UnicastRemoteObject implements RemoteGateKeeper {

    private int partitions;
    Registry registry;

    public GateKeeper(int partitions, Registry registry) throws RemoteException {
        this.registry = registry;
    }

    public RemoteLinda connectMe() throws RemoteException {
        RemoteLinda server = null;
        try {
            int accessPoint = (int) (Math.random()*partitions + 1);
            String url = "rmi://" + "localhost:7778" + "/Linda/server" + this.hashCode() + "" + accessPoint; // hashed for security

            server = (RemoteLinda) registry.lookup(url);

        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        return server;
    }
}
