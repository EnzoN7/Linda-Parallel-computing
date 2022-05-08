package linda.debug;

import linda.Tuple;
import linda.server.RemoteLinda;

import java.rmi.RemoteException;
import java.util.List;

public interface DebugRemoteLinda extends RemoteLinda {
    List<Tuple> getTuples() throws RemoteException;

    long getTupleSpaceSize() throws RemoteException;

    int getTuplesNumber() throws RemoteException;

    long getFreeMemory() throws RemoteException;

    long getTotalMemory() throws RemoteException;

    long getInUseMemory() throws RemoteException;

    long getMaxMemory() throws RemoteException;

    int getAvailableProcessors() throws RemoteException;
}
