package linda.crible;

import linda.Linda;
import linda.Tuple;
import linda.shm.CentralizedLinda;

public class SeqCrible extends Crible {

    public SeqCrible (Integer bound) {
        super(bound);
        for (int i = 2; i <= this.bound; i++){
            this.numberSpace.write(new Tuple(i));
        }
    }

    public String getPrimes() {

        for (int i = 2; i <= this.bound; i++) {
            if (this.numberSpace.tryRead(new Tuple(i)) == null) {
                continue;
            }
            takeMultipOf(i);
        }

        return this.numberSpace.readAll(new Tuple(Integer.class)).toString();
    }

    private void takeMultipOf (Integer n) {
        Integer multip = n*n;

        while (multip <= this.bound) {
            Tuple tuple = new Tuple(multip);
            this.numberSpace.tryTake(tuple);
            multip += n;
        }
    }
}
