package linda.shm.callables;

import linda.Tuple;
import linda.shm.ILindaMonitor;

import java.util.Collection;

public class TakeCallable extends LindaCallable {
	
	public TakeCallable(ILindaMonitor monitor, Tuple motif, boolean isTry, boolean isAll) {
		super(monitor, motif, isTry, isAll);
	}
	
	@Override
	public Collection<Tuple> call() throws Exception {
		
		Collection<Tuple> result = monitor.onReadingStart(motif, true, isTry, isAll);
		
		return result;
	}
}
