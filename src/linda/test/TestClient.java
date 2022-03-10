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

		(new Thread(){
			@Override
			public void run() {
				LindaClient linda = new LindaClient("rmi://localhost:7778/Linda");

				linda.write(new Tuple(2,'a'));
				Tuple  res2=linda.take(new Tuple(2,'a'));
				System.out.println("Le tuple recu est: " +res2);


				linda.write(new Tuple(2,'b'));
				Tuple result = linda.read(new Tuple(2,'c'));
				System.out.println("read worked : " + result);


				System.out.println("Fin du client");
			}
		}).start();

		(new Thread(){
			@Override
			public void run() {
				LindaClient linda = new LindaClient("rmi://localhost:7778/Linda");

				linda.write(new Tuple(2,'c'));
				Tuple result = linda.tryRead(new Tuple(2,'b'));
				System.out.println("read worked : " + result);


				System.out.println("Fin du client");
			}
		}).start();
	}	
}
