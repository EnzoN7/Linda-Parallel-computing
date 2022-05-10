package linda.multiserver;

import java.rmi.RemoteException;

import linda.Tuple;
import linda.multiserver.LindaClient;
import linda.multiserver.Manager;

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
				Tuple result = linda.read(new Tuple(2,'b'));
				System.out.println("read worked : " + result);

				linda.read(new Tuple(2,'l'));
				System.out.println("Fin du client");
				linda.write(new Tuple(2,'l'));
			}
		}).start();
	}	
}
