package linda.server;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
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

/** Client part of a client/server implementation of Linda.
 * It implements the Linda interface and propagates everything to the server it is connected to.
 * */
public class LindaClient implements Linda {
	
	protected RemoteLinda remoteLinda;
	
	private ExecutorService executor;
	
    /** Initializes the Linda implementation.
     *  @param serverURI the URI of the server, e.g. "rmi://localhost:4000/LindaServer" or "//localhost:4000/LindaServer".
     */
	// "rmi://" + InetAddress.getLocalHost().getHostAddress() + "/TestRMI"
    public LindaClient(String serverURI) {
        // TO BE COMPLETED
    	
		System.out.println("Lancement du client");
		
		this.executor = Executors.newFixedThreadPool(4); 
		
	    try {
	    	var registry = LocateRegistry.getRegistry(7774);
	    	
	        Remote r = registry.lookup(serverURI);
	        System.out.println(r);
	        if (r instanceof RemoteLinda) {
	        	remoteLinda = (RemoteLinda) r;
	        	System.out.println("RemoteLinda enregistr�");
	        }
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
		/*Future<Tuple> tuple = executor.submit(new Callable<Tuple>() {
			@Override
			public Tuple call() {
				try {	

					return remoteLinda.take(template);
				} catch (RemoteException e) {
					
					e.printStackTrace();
				}
				
				return null;
			}
		});

		
		try {
			return tuple.get();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		} catch (ExecutionException e) {
	
			e.printStackTrace();
		}*/

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
			
			remoteLinda.eventRegister(mode, timing, template, new RemoteCallbackObject() {
				
				/**
				 * 
				 */
				private static final long serialVersionUID = -6423820480450177226L;

				@Override
				public void call(Tuple t) throws RemoteException {
					callback.call(t);
					
				}
			});
		} catch (RemoteException e) {
			
			e.printStackTrace();
		}
	}

	@Override
	public void debug(String prefix) {
		
		
	}
    
    // TO BE COMPLETED

	public static void main(String[] args) throws RemoteException {
		Linda linda = new LindaClient("rmi://localhost:7778/TestRMI");
		
		Tuple motif0 = new Tuple(Integer.class, Character.class);
		Tuple motif1 = new Tuple(Character.class,Integer.class,Integer.class);
		
		//test1 
		//System.out.println("test1 : take bloquante");
		//Tuple res1 = linda.take(new Tuple(1, 1));
		//System.out.println("take failed");
		
		//test2
		System.out.println("test2 : take d'un tuple avec motif existant");
		linda.write(new Tuple(2,'a'));
		Tuple  res2=linda.take(new Tuple(2,'a'));
		System.out.println("Le tuple recu est: " +res2);

		//test3
		//System.out.println("test3 : read bloquante");
		//linda.read(new Tuple(1,2));
		//System.out.println("read failed");
		
		//test4
		System.out.println("test4 : read non bloquée");
		linda.write(new Tuple(2,'a'));
		linda.read(new Tuple(2,'a'));
		System.out.println("read worked");
		
		System.out.println("test5 : takeAll DOIT BLOQUER");
		Collection<Tuple> res5=linda.takeAll(new Tuple(Integer.class,'b'));
		System.out.println("test5 result:"+res5);
		
		//test5
		System.out.println("test5 : takeAll bloquante");
		linda.write(new Tuple('a',0,0));
		linda.write(new Tuple('a',1,1));
		linda.write(new Tuple(0,'b'));
		linda.write(new Tuple(1,'b'));
		
		
		//System.out.println("Ecriture d'un tuple (1,1)");
		
		class BCb implements Callback {
			private Tuple template;
			
			public BCb(Tuple template) throws RemoteException {
				super();
				
				this.template = template;
			}
			
			@Override
			public void call(Tuple t) {
				System.out.println("[BcB] t est " + t + " de template " + template);
			}
		}
		
		linda.eventRegister(eventMode.TAKE, eventTiming.IMMEDIATE, motif0, new BCb(motif0));
		
		System.out.println("Enregister callback.");

		//linda.write(new Tuple(100, 'b'));
		
	    System.out.println("Fin du client");
	}
}
