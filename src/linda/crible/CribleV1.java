package linda.crible;

import linda.Linda;
import linda.Tuple;

import java.util.concurrent.Semaphore;

public class CribleV1 extends Crible{
    public CribleV1 (Integer bound) {
        super(bound);
        for (int i = 2; i <= this.bound; i++){
            this.numberSpace.write(new Tuple(i));
        }
    }

    public String getPrimes() {
        Semaphore semaphore = new Semaphore(2-bound);

        for (int i = 2; i <= this.bound; i++) {
            (new cribleThread(i,this.bound,this.numberSpace,semaphore)).start();
        }

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return this.numberSpace.readAll(new Tuple(Integer.class)).toString();
    }

    class cribleThread extends Thread {
        int i;
        Integer bound;
        Linda numberSpace;
        Semaphore semaphore;
        public cribleThread(int i, Integer bound, Linda numberSpace, Semaphore semaphore) {
            super();
            this.i = i;
            this.bound = bound;
            this.numberSpace = numberSpace;
            this.semaphore = semaphore;
        }
        public void run () {
            if (this.numberSpace.tryRead(new Tuple(i)) == null) {
                this.semaphore.release();
            } else {
                takeMultipOf(i);
                this.semaphore.release();
            }
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
}
