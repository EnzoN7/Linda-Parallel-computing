package linda.search.basic;

import java.util.UUID;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;

import linda.*;
import linda.server.LindaClient;
import linda.server.RemoteCallback;

public class Manager implements Runnable {
	
    private Linda linda;

    private UUID reqUUID;
    
    private String pathname;
    
    private String search;
    
    public Manager(Linda linda, String pathname, String search) {
        this.linda = linda;
        this.pathname = pathname;
        this.search = search;
    }
    
    private void addSearch(String search) {
    	this.search = search;
        this.reqUUID = UUID.randomUUID();
        System.out.println("Search " + this.reqUUID + " for " + search);
        
        try {
			linda.eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.IMMEDIATE, new Tuple(Code.Result, this.reqUUID, String.class, Integer.class), new CbGetResult(this.reqUUID));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        linda.write(new Tuple(Code.Request, this.reqUUID, search));
    }

    private void loadData(String pathname) {
        try (Stream<String> stream = Files.lines(Paths.get(pathname))) {
            stream.limit(10000).forEach(s -> linda.write(new Tuple(Code.Value, s.trim())));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void waitForEndSearch() {
        linda.take(new Tuple(Code.Searcher, "done", this.reqUUID));
        linda.take(new Tuple(Code.Request, this.reqUUID, String.class)); // remove query
        System.out.println("query done");
    }
    
    private void setBest(UUID reqUUID, String s, Integer v) {
    	/*Boolean exists = getBest() == null;
    	
    	if(!exists) {
    		linda.write(new Tuple(Code.Best, s, v));
    	} else {
    		linda.take(new Tuple(Code.Best, String.class, Integer.class));
    		
    		linda.write(new Tuple(Code.Best, s, v));
    	}*/
    	
    	linda.write(new Tuple(Code.Best, reqUUID, s, v));
    }
    
    private Tuple takeBest(UUID reqUUID) {
    	Tuple bestTemplate = new Tuple(Code.Best, reqUUID, String.class, Integer.class);
    	
    	return linda.tryTake(bestTemplate);
    }

    private Tuple getBest(UUID reqUUID) {
    	Tuple bestTemplate = new Tuple(Code.Best, reqUUID, String.class, Integer.class);
    	
    	return linda.tryRead(bestTemplate);
    }
    
    private class CbGetResult implements Callback {
    	/**
		 * 
		 */
		private static final long serialVersionUID = 8902602521724901121L;
		private UUID reqUUID;
    	
        public CbGetResult(UUID reqUUID) throws RemoteException {
        	super();
        	
        	this.reqUUID = reqUUID;
		}

		public void call(Tuple t) {  // [ Result, ?UUID, ?String, ?Integer ]
            String s = (String) t.get(2);
            Integer v = (Integer) t.get(3);
            
            
            Tuple currentBest = getBest(reqUUID);
            
            
            if(currentBest != null) {
            	Integer currentV = (Integer)currentBest.get(3);
                if (v < currentV) {
                	takeBest(reqUUID);
                	
                	setBest(reqUUID, s, v);
                	
                    System.out.println("New best for " + reqUUID + " (" + v + "): \"" + s + "\"");
                    // linda debug
                }
                
            } else {
            	setBest(reqUUID, s, v);
            	
            	System.out.println("Best initialization for " + reqUUID + " (" + v + "): \"" + s + "\"");
                // linda debug
            }
            

            
            
            
            linda.eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.IMMEDIATE, new Tuple(Code.Result, reqUUID, String.class, Integer.class), this);
        }
    }

    public void run() {
        this.loadData(pathname);
        this.addSearch(search);
        this.waitForEndSearch();
    }
    
    public static void main(String[] args) {
    	if (args.length != 2) {
            System.err.println("linda.search.basic.Main search file.");
            return;
    	}
    	
		Linda linda = new LindaClient("rmi://localhost:7777/BasicSearch");
		
		Manager manager = new Manager(linda, args[1], args[0]);
		
		//(new Thread(manager)).start();
		
		manager.run();
	}
}
