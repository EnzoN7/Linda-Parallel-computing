package linda.shm.callables;

import linda.AsynchronousCallback;
import linda.Callback;
import linda.Tuple;

import java.util.concurrent.Callable;

public class RegisterCallable implements Callable<Void> {

	Callback callback;
    Tuple motif;

    public RegisterCallable(Callback callback, Tuple motif)  {
        this.callback = callback;
        this.motif = motif;
    }

    @Override
    public Void call() throws Exception {
        callback.call(this.motif);
        return null;
    }
}
