package linda.multiserver;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collection;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

/** Client part of a client/server implementation of Linda.
 * It implements the Linda interface and propagates everything to the server it is connected to.
 * */
public class LindaClient implements Linda {
	
	private RemoteLinda remoteLinda;

	

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
