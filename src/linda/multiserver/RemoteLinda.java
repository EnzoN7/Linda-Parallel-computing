package linda.multiserver;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

import linda.Tuple;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;

public interface RemoteLinda extends Remote {

	Tuple read(Tuple template) throws RemoteException;
	
	Tuple take(Tuple template) throws RemoteException;
	
	void write(Tuple t) throws RemoteException;
	
	Tuple tryTake(Tuple t) throws RemoteException;

	Tuple tryRead(Tuple t) throws RemoteException;

	Collection<Tuple> readAll(Tuple t) throws RemoteException;

	Collection<Tuple> takeAll(Tuple t) throws RemoteException;

	void eventRegister(eventMode mode, eventTiming timing, Tuple template, RemoteCallback callback) throws RemoteException;

}
