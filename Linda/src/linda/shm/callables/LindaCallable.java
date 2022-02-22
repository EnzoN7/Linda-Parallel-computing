package linda.shm.callables;

import java.util.Collection;
import java.util.concurrent.Callable;

import linda.Tuple;
import linda.shm.ILindaMonitor;

public abstract class LindaCallable implements Callable<Collection<Tuple>> {
	protected ILindaMonitor monitor;
	
	protected Tuple motif;

	protected boolean isTry;
	protected boolean isAll;
	
	public LindaCallable(ILindaMonitor monitor, Tuple motif, boolean isTry, boolean isAll) {
		this.monitor = monitor;
		this.motif = motif;
		this.isTry = isTry;
		this.isAll = isAll;
	}
}
