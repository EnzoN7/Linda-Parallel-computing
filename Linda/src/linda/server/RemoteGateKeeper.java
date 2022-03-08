package linda.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteGateKeeper extends Remote {
    public RemoteLinda connectMe() throws RemoteException;
}
