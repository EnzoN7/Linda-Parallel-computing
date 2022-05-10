package linda.multiserver;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public abstract class RemoteCallbackObject extends UnicastRemoteObject implements RemoteCallback {

	protected RemoteCallbackObject() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	} 
	

}

