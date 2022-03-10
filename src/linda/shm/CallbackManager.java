package linda.shm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

public class CallbackManager implements ICallbackManager {
	
	private Map<Tuple, CopyOnWriteArrayList<Callback>> readCallbacks;
	
	private Map<Tuple, CopyOnWriteArrayList<Callback>> takeCallbacks;
	
	private ReadWriteLock mutex;
	
	private Linda linda;
	
	
	public CallbackManager(Linda linda) {
		readCallbacks = new HashMap<>();
		
		takeCallbacks = new HashMap<>();
		
		mutex = new ReentrantReadWriteLock();
		
		
		this.linda = linda;
	}
	
	@Override
	public void onWrite(Tuple tuple) throws InterruptedException {
		//mutex.readLock().lock();
		System.out.println("CallbackManager: OnRead::" + tuple + "::" + readCallbacks.keySet().size());

		///for (Entry<Tuple, List<Callback>> entry : readCallbacks.entrySet()) {
		for (var it = readCallbacks.entrySet().iterator(); it.hasNext(); ) {
			var entry = it.next();
			System.out.println("CallbackManager: Motif::" + entry.getKey());
			if(tuple.matches(entry.getKey())) {
				System.out.println("CallbackManager: Appel callback read");
				
				var callbacks = readCallbacks.remove(entry.getKey());
				
				for(var it2 = callbacks.iterator(); it2.hasNext(); )  {
					var callback = it2.next();
					
					System.out.println(Thread.currentThread().getId() + "# CallbackManager : call");
					
					
					callback.call(tuple);
					
				}
			}
		}

		System.out.println(Thread.currentThread().getId() + "# CallbackManager: OnTake::" + tuple + "::" + takeCallbacks.keySet().size());
		//for (Entry<Tuple, List<Callback>> entry : takeCallbacks.entrySet()) {
		for (var it = takeCallbacks.entrySet().iterator(); it.hasNext(); ) {
			var entry = it.next();
			System.out.println(Thread.currentThread().getId() + "# CallbackManager: Motif::" + entry.getKey());
			if(tuple.matches(entry.getKey())) {
				System.out.println(Thread.currentThread().getId() + "# CallbackManager: Appel callback take");

				if(linda.tryTake(tuple) != null) {
					System.out.println(Thread.currentThread().getId() + "# CallbackManager:: remove by tryTake " + tuple);
				}
				
				var callbacks = takeCallbacks.remove(entry.getKey());
				
				for(var it2 = callbacks.iterator(); it2.hasNext(); )  {
					var callback = it2.next();
					
					System.out.println(Thread.currentThread().getId() + "# CallbackManager : call");
					
					
					callback.call(tuple);
					
				}
				
				System.out.println(takeCallbacks.keySet());
				System.out.println(Thread.currentThread().getId() + "# CallbackManager: After Calling::" + tuple + "::" + takeCallbacks.keySet().size());
			
				
			}
		}
		

		//mutex.readLock().unlock();
	}

	@Override
	public void onRead(Tuple tuple) {
		/*System.out.println("CallbackManager: OnRead::" + tuple + "::" + readCallbacks.keySet().size());
		for (Entry<Tuple, List<Callback>> entry : readCallbacks.entrySet()) {
			System.out.println("CallbackManager: Motif::" + entry.getKey());
			if(tuple.matches(entry.getKey())) {
				System.out.println("CallbackManager: Appel callback onRead");
				for(Callback callback : entry.getValue()) 
					callback.call(tuple);
				readCallbacks.remove(entry.getKey());
			}
		}*/
	}

	@Override
	public void onTake(Tuple tuple) {
		/*System.out.println("CallbackManager: OnTake::" + tuple );;
		for (Entry<Tuple, List<Callback>> entry : takeCallbacks.entrySet()) {
			System.out.println("CallbackManager: Motif::" + entry.getKey());
			if(entry.getKey().matches(tuple)) {
				System.out.println("CallbackManager: Appel callback onTake");
				for(Callback callback : entry.getValue()) 
					callback.call(tuple);
				takeCallbacks.remove(entry.getKey());
			}
		}*/
	}

	@Override
	public void registerReadCallback(Tuple motif, Callback callback) {
		//mutex.writeLock().lock();
		if(readCallbacks.containsKey(motif) && readCallbacks.get(motif) != null) {
			System.out.println("Register existing callbacks");
			CopyOnWriteArrayList<Callback> callbacks = readCallbacks.get(motif);
			callbacks.add(callback);
			readCallbacks.put(motif, callbacks);
		} else {
			System.out.println("Register callbacks");
			CopyOnWriteArrayList<Callback> callbacks = new CopyOnWriteArrayList<Callback>();
			callbacks.add(callback);
			readCallbacks.put(motif, callbacks);
		}
		//mutex.writeLock().unlock();
	}

	@Override
	public void registerTakeCallback(Tuple motif, Callback callback) {
		//mutex.writeLock().lock();
		System.out.println(takeCallbacks.keySet());
		if(takeCallbacks.containsKey(motif) && takeCallbacks.get(motif) != null) {
			CopyOnWriteArrayList<Callback> callbacks = takeCallbacks.get(motif);
			callbacks.add(callback);
			takeCallbacks.put(motif, callbacks);

			System.out.println(Thread.currentThread().getId() + "# CallbackManager :: registerTakeCallback IN EXISTING  " + takeCallbacks.keySet().size());
		} else {
			CopyOnWriteArrayList<Callback> callbacks = new CopyOnWriteArrayList<Callback>();
			callbacks.add(callback);
			takeCallbacks.put(motif, callbacks);

			System.out.println(Thread.currentThread().getId() + "# CallbackManager :: registerTakeCallback NEW " + takeCallbacks.keySet().size());
		}
		//mutex.writeLock().unlock();
	}

}
