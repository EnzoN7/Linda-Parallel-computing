package linda.shm;

import linda.Callback;
import linda.Tuple;

public interface ICallbackManager extends ITupleSpaceObserver {

	void registerReadCallback(Tuple motif, Callback callback);
	
	void registerTakeCallback(Tuple motif, Callback callback);
	
}
