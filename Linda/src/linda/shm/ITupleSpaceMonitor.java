package linda.shm;

public interface ITupleSpaceMonitor {

	void startReading() throws InterruptedException;
	
	void endReading() throws InterruptedException;
	
	void startWriting() throws InterruptedException;
	
	void endWriting() throws InterruptedException;
}
