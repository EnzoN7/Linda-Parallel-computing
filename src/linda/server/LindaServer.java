package linda.server;

import linda.Callback;
import linda.Tuple;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.shm.CentralizedLinda;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;

public class LindaServer extends UnicastRemoteObject implements RemoteLinda  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8835789050647334792L;
	
	private CentralizedLinda kernel;
	
	public LindaServer() throws RemoteException {
		kernel = new CentralizedLinda();
	}
	
	public void listen() {
		// TODO Auto-generated method stub
		
	}

	public CentralizedLinda getKernel() {
		return kernel;
	}
	
	@Override
	public Tuple read(Tuple template) throws RemoteException {
		return kernel.read(template);
	}
	
	@Override
	public Tuple take(Tuple template) throws RemoteException {
		return kernel.take(template);
	}
	
	@Override
	public void write(Tuple t) throws RemoteException {
		kernel.write(t);
	}
	
	@Override
	public Tuple tryTake(Tuple t) throws RemoteException {
		return kernel.tryTake(t);
	}
	
	@Override
	public Tuple tryRead(Tuple t) throws RemoteException {
		return kernel.tryRead(t);
	}
	@Override
	public Collection<Tuple> readAll(Tuple t) throws RemoteException {
		return kernel.readAll(t);
	}
	@Override
	public Collection<Tuple> takeAll(Tuple t) throws RemoteException {
		return kernel.takeAll(t);
	}
	
	@Override
	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, RemoteCallback callback) {
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

	public static void main(String[] args) {
		try {

			var registry = LocateRegistry.createRegistry(7774);
			
			LindaServer server = new LindaServer();
			
			
			String url = "rmi://" + "localhost:7778" + "/TestRMI";
		    System.out.println("Enregistrement de l'objet avec l'url : " + url);
		    registry.rebind(url, server);

		    System.out.println("Serveur lanc�");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
