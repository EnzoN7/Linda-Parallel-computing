package linda.callbacks;

import linda.Callback;
import linda.Tuple;

public class HelloCallback implements Callback {
    @Override
    public void call(Tuple t) {
        System.out.println("HelloCallback : Hello " + t + " !");
    }
}
