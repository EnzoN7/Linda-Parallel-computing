package linda.multiserver;

import linda.Callback;
import linda.Tuple;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.shm.CentralizedLinda;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;

public class LindaServer extends UnicastRemoteObject implements RemoteLinda  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8835789050647334792L;
	
	private CentralizedLinda kernel;

	private int serverAdd;

	private Manager manager;
	
	public LindaServer(int serverAdd, Manager manager) throws RemoteException {
		this.kernel = new CentralizedLinda();
		this.serverAdd = serverAdd;
		this.manager = manager;
	}
	
	public void listen() {
		// TODO Auto-generated method stub
		
	}

	public CentralizedLinda getKernel() {
		return kernel;
	}
	
	@Override
	public Tuple read(Tuple template) throws RemoteException {
		Tuple result = kernel.tryRead(template);
		if (result == null) {
			result = (Tuple) this.manager.redistribute(this.serverAdd, "readNoRedirect", template);
		}
		if (result == null) {
			this.manager.register(template,"readNoRedirect");
		}
		return result;
	}

	public Tuple readNoRedirect(Tuple template) throws RemoteException {
		return kernel.tryRead(template);
	}
	
	@Override
	public Tuple take(Tuple template) throws RemoteException {
		Tuple result = kernel.tryTake(template);
		if (result == null) {
			result = (Tuple) this.manager.redistribute(this.serverAdd, "takeNoRedirect", template);
		}
		if (result == null) {
			this.manager.register(template,"takeNoRedirect");
		}
		return result;
	}

	public Tuple takeNoRedirect(Tuple template) throws RemoteException {
		return kernel.tryTake(template);
	}
	
	@Override
	public void write(Tuple t) throws RemoteException {
		System.out.println("Writing " + t + " to server: "  + this.serverAdd);
		kernel.write(t);
		this.manager.noticeOn(t);
	}
	
	@Override
	public Tuple tryTake(Tuple t) throws RemoteException {
		Tuple result = kernel.tryTake(t);
		if (result == null) {
			result = (Tuple) this.manager.redistribute(this.serverAdd, "tryTakeNoRedirect", t);
		}
		return result;
	}

	public Tuple tryTakeNoRedirect(Tuple t) throws RemoteException {
		return kernel.tryTake(t);
	}
	
	@Override
	public Tuple tryRead(Tuple t) throws RemoteException {
		Tuple result = kernel.tryRead(t);
		if (result == null) {
			result = (Tuple) this.manager.redistribute(this.serverAdd, "tryReadNoRedirect", t);
		}
		return result;
	}

	public Tuple tryReadNoRedirect(Tuple t) throws RemoteException {
		return kernel.tryRead(t);
	}

	@Override
	public Collection<Tuple> readAll(Tuple t) throws RemoteException {
		Collection<Tuple> result = kernel.readAll(t);
		if (result == null) {
			result = (Collection<Tuple>) this.manager.redistribute(this.serverAdd, "readAllNoRedirect", t);
		}
		return result;
	}

	public Collection<Tuple> readAllNoRedirect(Tuple t) throws RemoteException {
		return kernel.readAll(t);
	}

	@Override
	public Collection<Tuple> takeAll(Tuple t) throws RemoteException {
		Collection<Tuple> result = kernel.takeAll(t);
		if (result == null) {
			result = (Collection<Tuple>) this.manager.redistribute(this.serverAdd, "takeAllNoRedirect", t);
		}
		return result;
	}

	public Collection<Tuple> takeAllNoRedirect(Tuple t) throws RemoteException {
		return kernel.takeAll(t);
	}
	
	@Override
	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, RemoteCallback callback) throws RemoteException {
		kernel.eventRegister(mode, timing, template, new Callback() {
			
			@Override
			public void call(Tuple t) {
				try {
					callback.call(t);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		this.manager.redistribute(this.serverAdd, "eventRegisterNoRedirect", mode, timing, template, callback);
	}

	public void eventRegisterNoRedirect(eventMode mode, eventTiming timing, Tuple template, RemoteCallback callback) throws RemoteException {
		kernel.eventRegister(mode, timing, template, new Callback() {

			@Override
			public void call(Tuple t) {
				try {
					callback.call(t);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

}
