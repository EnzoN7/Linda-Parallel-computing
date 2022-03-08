package linda.test;

import java.rmi.RemoteException;
import java.util.Collection;

import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.server.LindaClient;
import linda.server.LindaServer;
import linda.server.Manager;
import linda.server.RemoteCallback;

public class TestClient {

	public static void main(String[] args) throws RemoteException {

		Manager manager = new Manager();

		manager.startUp();

		LindaClient linda = new LindaClient("rmi://localhost:7778/Linda");
		
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
		

		//test5
		System.out.println("test5 : takeAll bloquante");
		linda.write(new Tuple('a',0,0));
		linda.write(new Tuple('a',1,1));
		linda.write(new Tuple(0,'b'));
		linda.write(new Tuple(1,'b'));
		
		//test6
		Collection<Tuple> res5=linda.takeAll(new Tuple(Integer.class,'b'));
		System.out.println("test5 result:"+res5);
		
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
