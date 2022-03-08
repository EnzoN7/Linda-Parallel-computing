package linda.server;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;

/** Client part of a client/server implementation of Linda.
 * It implements the Linda interface and propagates everything to the server it is connected to.
 * */
public class LindaClient implements Linda {
	
	private RemoteLinda remoteLinda;

	
    /** Initializes the Linda implementation.
     *  @param serverURI the URI of the server, e.g. "rmi://localhost:4000/LindaServer" or "//localhost:4000/LindaServer".
     */
	// "rmi://" + InetAddress.getLocalHost().getHostAddress() + "/TestRMI"
    public LindaClient(String serverURI) {
    	
		System.out.println("Lancement du client");

		try {
			String url = "rmi://" + "localhost:7778" + "/Linda";

			Registry registry = LocateRegistry.getRegistry(7774);
			RemoteGateKeeper gateKeeper = (RemoteGateKeeper) registry.lookup(url);

			this.remoteLinda = gateKeeper.connectMe();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void write(Tuple t) {
		try {
			remoteLinda.write(t);
		} catch (RemoteException e) {
			
			e.printStackTrace();
		}
	}

	@Override
	public Tuple take(Tuple template) {
		try {
			return remoteLinda.take(template);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Tuple read(Tuple template) {
		try {	
			return remoteLinda.read(template);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public Tuple tryTake(Tuple template) {
		try {
			return remoteLinda.tryTake(template);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
		
		
	}

	@Override
	public Tuple tryRead(Tuple template) {
		try {
			return remoteLinda.tryRead(template);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
		
		
	}

	@Override
	public Collection<Tuple> takeAll(Tuple template) {
		try {
			return remoteLinda.takeAll(template);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
		
		
	}


	@Override
	public Collection<Tuple> readAll(Tuple template) {
		try {
			return remoteLinda.readAll(template);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
		
		
	}

	@Override
	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
		try {
			
			remoteLinda.eventRegister(mode, timing, template, (RemoteCallback) callback);
		} catch (RemoteException e) {
			
			e.printStackTrace();
		}
	}

	@Override
	public void debug(String prefix) {

	}
}
