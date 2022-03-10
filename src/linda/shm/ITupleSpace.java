package linda.shm;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import linda.Tuple;

public interface ITupleSpace extends Iterable<Tuple> {

	List<Tuple> getTuples();
	
	// retourne un sous espace de tuple qui est lui même un espace de tuple
	ITupleSpace find(Predicate<Tuple> filter) throws InterruptedException;

	ITupleSpace find(Tuple motif) throws InterruptedException;
	
	Optional<Tuple>  findOne(Predicate<Tuple> filter) throws InterruptedException;
	
	Optional<Tuple> findOne(Tuple motif) throws InterruptedException;  //////
	
	boolean exists(Predicate<Tuple> filter) throws InterruptedException;
	
	boolean exists(Tuple motif) throws InterruptedException;
	
	ITupleSpace take(Predicate<Tuple> filter) throws InterruptedException; //////
	
	Optional<Tuple> takeOne(Predicate<Tuple> filter) throws InterruptedException;
	
	Optional<Tuple> takeOne(Tuple motif) throws InterruptedException;
	
	void add(ITupleSpace tupleSpace) throws InterruptedException;
	
	void add(Tuple...tuples) throws InterruptedException;
	
	void add(Tuple tuple) throws InterruptedException;  //////
	
	void print();

	boolean remove(Tuple tuple) throws InterruptedException;
	
}
