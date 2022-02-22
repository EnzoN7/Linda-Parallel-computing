package linda.crible;

import linda.Linda;
import linda.Tuple;
import linda.shm.CentralizedLinda;

public abstract class Crible {
    protected Linda numberSpace;
    protected Integer bound;

    public Crible (Integer bound) {
        this.bound = bound;
        this.numberSpace = new CentralizedLinda();
    }

    public abstract String getPrimes();
}
