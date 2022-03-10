package linda.server;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import linda.Callback;
import linda.Tuple;

public interface RemoteCallback extends Remote  {

	void call(Tuple t) throws RemoteException;
}
