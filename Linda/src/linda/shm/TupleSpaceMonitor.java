package linda.shm;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TupleSpaceMonitor implements ITupleSpaceMonitor {

	private final Lock mutex = new ReentrantLock();
	
	private int waitingReaders;
	
	private int readers;
	
	private boolean writing;
	
	private final Condition haveAccessToRead = mutex.newCondition();

	private final Condition haveAccessToWrite = mutex.newCondition();

	@Override
	public void startReading() throws InterruptedException {
		mutex.lock();
		
		printState("beforeStartReading#" + Thread.currentThread().getId());
		
		waitingReaders++;
		
		if(writing) {
			haveAccessToRead.await();
		}
		
		readers++;
		
		waitingReaders--;
		
		mutex.unlock();
	}

	@Override
	public void endReading() throws InterruptedException {
		mutex.lock();
		
		printState("beforeEndReading#" + Thread.currentThread().getId());
		
		readers--;
		
		// Quand il n'y a plus de lecteurs, on signale que l'accés est possible pour un redacteur
		if(readers == 0) {
			haveAccessToWrite.signal();
		}
		
		mutex.unlock();
	}

	@Override
	public void startWriting() throws InterruptedException {
		mutex.lock();
		
		printState("beforeStartWriting#" + Thread.currentThread().getId());

		if (readers > 0 || writing || waitingReaders > 0 ) {
			haveAccessToWrite.await();
		}
		
		writing = true;
		
		mutex.unlock();
	}

	@Override
	public void endWriting() throws InterruptedException {
		mutex.lock();
		
		printState("beforeEndWriting#" + Thread.currentThread().getId());
		
		writing = false;

		if (waitingReaders == 0) {
			haveAccessToWrite.signal();
		} else {
			haveAccessToRead.signalAll();
		}

		mutex.unlock();
	}

	private void printState(String operation) {
		System.out.println("[" + operation + "]readers : " + readers + ", writing: " + writing + ", waitingReaders: " + waitingReaders);
	}
}
