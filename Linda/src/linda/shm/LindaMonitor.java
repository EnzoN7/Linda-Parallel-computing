package linda.shm;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import linda.Tuple;

// Listener
public class LindaMonitor implements ILindaMonitor {
	
	private Lock mutex;
	
	// On associe à chaque opération de type read une variable condition et on regroupe le tout dans une Map
	// De cette maniére, si on tente de read sur un même motif, on utilisera la même variable-condition.
	// Le prédicat d'accepatation est : tant qu'on ne trouve pas de tuple matchant le motif, on await
	// A chaque write, la methode OnWriting sera appelé qui reveillera un thread qui attend sur le motif
	private Map<Tuple, Condition> readingConditions;
	
	private Map<Condition, Integer> waitingThreads;
	
	private ITupleSpace tupleSpace;
	
	private ICallbackManager callbackManager;
	
	public LindaMonitor(ITupleSpace tupleSpace, ICallbackManager callbackManager) {
		mutex = new ReentrantLock();
		
		readingConditions = new HashMap<Tuple, Condition>();
		
		waitingThreads = new HashMap<Condition, Integer>();
		
		this.tupleSpace = tupleSpace;
		this.callbackManager = callbackManager;
	}
	
	@Override
	public Collection<Tuple> onReadingStart(Tuple motif, boolean isTake, boolean isTry, boolean isAll) throws InterruptedException {
		Condition readingCondition = null;
		
		System.out.println("#" + Thread.currentThread().getId()  + ".. on essaye de read le motif:" + motif);
		
		tupleSpace.print();
		
		if(!isTry) {
			if(readingConditions.containsKey(motif)) {
				readingCondition = readingConditions.get(motif);
			} else {
				readingCondition = mutex.newCondition();
				
				readingConditions.put(motif, readingCondition);
				
				waitingThreads.put(readingCondition, 0);
				
				System.out.println("#" + Thread.currentThread().getId() +" "+ motif +" contains: " + readingConditions.containsKey(motif) + " with : " + waitingThreads.get(readingCondition));
			}
		
			mutex.lock();
			
			while(!tupleSpace.exists(motif)) {
				System.out.println("#" + Thread.currentThread().getId()  + " Tuple " + motif + " still doesn't exists;");
				
				tupleSpace.print();
				
				waitingThreads.put(readingCondition, waitingThreads.get(readingCondition) + 1);
				
				readingCondition.await();	
			}
			
	
			mutex.unlock();
			System.out.println("#" + Thread.currentThread().getId()  + " motif " + motif  + " existe");
		}

		List<Tuple> resultList = new ArrayList<Tuple>();
		Optional<Tuple> result = Optional.empty();
		if (isAll) {
			resultList = (isTake ? tupleSpace.take(t -> t.matches(motif)) : tupleSpace.find(motif)).getTuples();
		} else {
			result = isTake ? tupleSpace.takeOne(motif) : tupleSpace.findOne(motif);
		}

		if(result.isPresent() || !resultList.isEmpty()) {
			if(!isTry) {
				System.out.println("#" + Thread.currentThread().getId()  + " trying to clear " + motif);
			
				// remove reading condition of the motif only if there are no longer threads waiting for this motif
				this.tryClearReadingCondition(motif);
			
				readingConditions.remove(motif);
			}
			
			if (result.isPresent()) {
				// On vérifie si il existe des callbacks associés au tuple trouvé
				/*if(!isTake)
					callbackManager.onRead(result.get());
				else
					callbackManager.onTake(result.get());*/

				List<Tuple> returnElm = new ArrayList<Tuple>();
				returnElm.add(result.get());
				return returnElm;
			} else {
				return resultList;
			}
		} else {
			return null;
		}
	}

	private void tryClearReadingCondition(Tuple motif) {
		if(readingConditions.containsKey(motif) && waitingThreads.containsKey(readingConditions.get(motif))) {
			int waitingThreadNumber = waitingThreads.get(readingConditions.get(motif));
			
			if(waitingThreadNumber > 0)
				waitingThreads.put(readingConditions.get(motif), waitingThreads.get(readingConditions.get(motif)) - 1);
			else {
				readingConditions.remove(motif);

				waitingThreads.remove(readingConditions.get(motif));
				
				System.out.println("#" + Thread.currentThread().getId()  + " remove condition " + motif);
			}
		}
	}

	
	@Override
	public void onWritingEnd(Tuple tuple) throws InterruptedException {
		System.out.println("Start# OnWriting");

		System.out.println("#" + Thread.currentThread().getId()  + ".. trying to signal that we have write " + tuple);
		System.out.println("#" + Thread.currentThread().getId()  + ".. we have " + readingConditions.size() + " condition-variables.");
		
		callbackManager.onWrite(tuple);

		Condition condition;
		
		mutex.lock();
		for (Entry<Tuple, Condition> entry : readingConditions.entrySet()) {
			
			System.out.println("\t -" + entry.getKey() + " with " + waitingThreads.get(entry.getValue()) + " threads waiting for it, do " + tuple + " matches it ? Response: " + tuple.matches(entry.getKey()));
			if(tuple.matches(entry.getKey())) {
				condition = entry.getValue();
				
				System.out.println("Signal to all threads waiting to read " + entry.getKey().toString() + " that he can.");
				
				// On reveille un thread qui attend de lire le motif
				condition.signalAll();
				break;
			}
		}
		mutex.unlock();
	
		
		System.out.println("End# OnWriting");
	}

}
