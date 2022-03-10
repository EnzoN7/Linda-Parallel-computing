package linda.search.basic;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import linda.*;
import linda.server.LindaServer;

public class Main  {


	public static void main(String args[]) {
        //Linda linda = new linda.shm.CentralizedLinda();
    	//Linda linda = new Linda
    	LindaServer server;
    	
		try {
			server = new LindaServer();
			
			var registry = LocateRegistry.createRegistry(7774);
			
			String url = "rmi://" + "localhost:7777" + "/BasicSearch";
		    System.out.println("Enregistrement de l'objet avec l'url : " + url);
		    registry.rebind(url, server);

		    System.out.println("Serveur BasicSearch lancé");
	    	
	        //Manager manager = new Manager(linda, args[1], args[0]);
	        //Searcher searcher = new Searcher(linda);
	        //(new Thread(manager)).start();
	        //(new Thread(searcher)).start();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

    }
}
