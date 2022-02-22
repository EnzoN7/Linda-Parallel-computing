package linda.crible;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CribleNoLinda {
    Lock lock = new ReentrantLock();
    private final Condition haveAccessToRead = lock.newCondition();
    private final Condition haveAccessToWrite = lock.newCondition();
    ExecutorService exec;
    int nbReaders = 0;
    int nbWaiting = 0;
    boolean writing = false;
    Integer bound;

    ArrayList<Integer> list = new ArrayList<Integer>();

    public CribleNoLinda(Integer bound) {
        this.bound = bound;
        this.exec = Executors.newFixedThreadPool(2*bound);
        for (int i = 2; i <= bound; i++) {
            write(i);
        }
    }

    void write(Integer i) {
        lock.lock();
        if (writing || nbReaders > 0 || nbWaiting > 0) {
            try {
                haveAccessToWrite.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writing = true;
        list.add(i);
        lock.unlock();
        endWrite();
    }

    void endWrite() {
        lock.lock();
        writing = false;
        if (nbWaiting == 0) {
            haveAccessToWrite.signal();
        } else {
            haveAccessToRead.signalAll();
        }
        lock.unlock();
    }

    void remove(Integer i) {
        lock.lock();
        if (writing || nbReaders > 0) {
            try {
                haveAccessToWrite.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writing = true;
        list.remove(i);
        lock.unlock();
        endWrite();
    }

    boolean contains(Integer i) {
        boolean result;
        lock.lock();
        if (writing) {
            try {
                haveAccessToRead.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        nbReaders++;
        result = this.list.contains(i);
        nbReaders--;
        if (nbReaders == 0) {
            haveAccessToWrite.signal();
        }
        lock.unlock();
        return result;
    }

    public String getPrimes() {
        Semaphore semaphore = new Semaphore(2-bound);

        for (int i = 2; i <= this.bound; i++) {
            exec.submit((new cribleThreadNoLinda(i,this.bound,semaphore)));
        }

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return this.list.toString();
    }

    class cribleThreadNoLinda extends Thread {
        int i;
        Integer bound;
        Semaphore semaphore;
        public cribleThreadNoLinda(int i, Integer bound, Semaphore semaphore) {
            super();
            this.i = i;
            this.bound = bound;
            this.semaphore = semaphore;
        }
        public void run () {
            if (!contains(i)) {
                this.semaphore.release();
            } else {
                takeMultipOf(i);
                this.semaphore.release();
            }
        }
        private void takeMultipOf (Integer n) {
            Integer multip = n*n;

            while (multip <= this.bound) {
                remove(multip);
                multip += n;
            }
        }
    }

}

