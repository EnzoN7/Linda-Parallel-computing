package linda.shm;

import linda.Tuple;

import java.util.Collection;
import java.util.concurrent.locks.Lock;

public interface ILindaMonitor {
	Collection<Tuple> onReadingStart(Tuple motif, boolean isTake, boolean isTry, boolean isAll) throws InterruptedException;
	
	void onWritingEnd(Tuple motif)throws InterruptedException;
}
