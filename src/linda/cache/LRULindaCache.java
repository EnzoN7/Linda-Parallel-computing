package linda.cache;

import linda.Tuple;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LRULindaCache implements ILindaCache {

    private int capacity;
    //private ReentrantReadWriteLock lock;

    //private Map<Tuple, ConcurrentLinkedDeque<Tuple>> linkedListNodeMap;

    private Map<Tuple, Deque<Tuple>> linkedListNodeMap;

    public LRULindaCache(int capacity) {
        this.capacity = capacity;
        //lock = new ReentrantReadWriteLock();

        linkedListNodeMap = new HashMap<>();
    }

    @Override
    public boolean put(Tuple key, Tuple value) {
        //lock.writeLock().lock();
        try {
            if(linkedListNodeMap.containsKey(key)) {
                Deque<Tuple> deque = linkedListNodeMap.get(key);
                if(!deque.contains(value)) {
                    if(deque.size() == capacity) {
                        deque.removeLast();
                    }
                    deque.addFirst(value);
                }
            } else {
                Deque<Tuple> deque = new LinkedList<>();


                deque.addFirst(value);

                linkedListNodeMap.put(key, deque);
            }
        } finally {
            //lock.writeLock().unlock();
        }
        return false;
    }

    @Override
    public Optional<Tuple> get(Tuple key) {
        //lock.readLock().lock();
        try {
            for(var template : linkedListNodeMap.keySet()) {
                if(key.matches(template)) {
                    Deque<Tuple> deque = linkedListNodeMap.get(key);

                    if(deque.isEmpty()) {
                        linkedListNodeMap.remove(key);
                        return Optional.empty();
                    } else {
                        Tuple tuple = deque.peekFirst();

                        //System.out.println("CACHE GET :  " + tuple);
                        return Optional.of(tuple);
                    }
                }
            }
           /* if(linkedListNodeMap.containsKey(key)) {

            }*/

            return Optional.empty();
        } finally {
            //lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        //this.lock.readLock().lock();
        try {
            int result = 0;
            for(Tuple key : linkedListNodeMap.keySet()) {
                result += linkedListNodeMap.get(key).size();
            }
            return result;
        } finally {
            //this.lock.readLock().unlock();
        }
    }

    @Override
    public int size(Tuple template) {
        return linkedListNodeMap.containsKey(template) ? linkedListNodeMap.get(template).size() : 0;
    }

    @Override
    public boolean isEmpty() {
        return linkedListNodeMap.isEmpty();
    }

    @Override
    public void clear() {
        //this.lock.writeLock().lock();

        try {
            linkedListNodeMap.clear();
        } finally {
            //this.lock.writeLock().unlock();
        }
    }

    @Override
    public void removeOne(Tuple key) {
        //this.lock.writeLock().lock();

        try {
            if(linkedListNodeMap.containsKey(key)) {
                Deque<Tuple> deque = linkedListNodeMap.get(key);

                if(deque.isEmpty()) {
                    linkedListNodeMap.remove(key);
                } else {
                    deque.removeFirst();

                    if(deque.isEmpty()) {
                        linkedListNodeMap.remove((key));
                    }
                    //System.out.println("removeOne : " + key);
                }
            }

        } finally {
            //this.lock.writeLock().unlock();
        }
    }

    @Override
    public void removeAll(Tuple key) {
        //this.lock.writeLock().lock();

        try {
            linkedListNodeMap.remove(key);
        } finally {
            //this.lock.writeLock().unlock();
        }
    }

    @Override
    public Map<Tuple, Deque<Tuple>> getMap() {
        return Collections.unmodifiableMap(this.linkedListNodeMap);
    }
}
