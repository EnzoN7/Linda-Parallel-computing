package linda.shm.callables;

import linda.Tuple;
import linda.shm.ILindaMonitor;

import java.util.Collection;

public class ReadCallable extends LindaCallable {
	
	public ReadCallable(ILindaMonitor monitor, Tuple motif, boolean isTry, boolean isAll) {
		super(monitor, motif, isTry, isAll);
	}
	
	@Override
	public Collection<Tuple> call() throws Exception {

		Collection<Tuple> result = monitor.onReadingStart(motif, false, isTry, isAll);
		/* code critique */
		/* Conditions pour entrer dans ce bloc :
		 * 	- Un tuple correspandant au motif est trouvé*/
		
		return result;
	}
}
