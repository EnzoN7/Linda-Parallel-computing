package linda.shm;

import java.util.function.Predicate;

import linda.Tuple;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

public class TupleSpace implements ITupleSpace {

	// modéle redacteur-lecteurs
	private static ITupleSpaceMonitor monitor;

	private List<Tuple> tuples;

	public List<Tuple> getTuples() {
		return tuples;
	}

	public TupleSpace() {
		tuples = new ArrayList<Tuple>();
		
		monitor = new TupleSpaceMonitor();
	}
	
	public TupleSpace(List<Tuple> tuples) {
		this.tuples = tuples;
		
		monitor = new TupleSpaceMonitor();
	}
	
	@Override
	public ITupleSpace find(Predicate<Tuple> filter) throws InterruptedException {
		ITupleSpace result; 
		
		System.out.println("monitor.startReading();");
		monitor.startReading();
		result = new TupleSpace((new ArrayList<Tuple>(tuples))
				.stream()
				.filter(filter)
				.collect(Collectors.toList()));
		monitor.endReading();
		
		return result;
	}

	@Override
	public ITupleSpace find(Tuple motif) throws InterruptedException {
		ITupleSpace result;
		
		// L'instruction au dessous est critique et bloquante (entouré implicitement par startRead et endRead)
		result = this.find(t -> filter(t,motif));
		
		return result;
	}

	@Override
	public Optional<Tuple> findOne(Predicate<Tuple> filter) throws InterruptedException {
		Optional<Tuple> result;
		System.out.println("monitor.startReading();");
		monitor.startReading();
		result = (new ArrayList<Tuple>(tuples))
				.stream()
				.filter(filter)
				.findFirst();
		monitor.endReading();
		
		return result;
	}

	@Override
	public Optional<Tuple> findOne(Tuple motif) throws InterruptedException {
		Optional<Tuple> tuple;
		
		// L'instruction au dessous est critique et bloquante (entouré implicitement par startRead et endRead)
		tuple = this.findOne(t -> filter(t,motif));
		
		System.out.println("is " + motif + " presents :" + tuple.isPresent());
		
		return tuple;
	}

	@Override
	public boolean exists(Predicate<Tuple> filter) throws InterruptedException {
		return (new ArrayList<Tuple>(tuples))
				.stream()
				.filter(filter)
				.findFirst()
				.isPresent();
	}

	@Override
	public boolean exists(Tuple motif) throws InterruptedException {
		return this.exists(t -> filter(t,motif));
	}

	@Override
	public ITupleSpace take(Predicate<Tuple> filter)  throws InterruptedException {
		ITupleSpace result;
		
		// L'instruction au dessous est critique et bloquante (entouré implicitement par startRead et endRead)
		result = this.find(filter);
		
		// On va considérer que la suppression est une opération d'ecriture (A CORRIGER!!)
		monitor.startWriting();
		for(Tuple tuple : result)
			this.remove(tuple);
		monitor.endWriting();
	
		
		return result;
	}

	@Override
	public Optional<Tuple> takeOne(Predicate<Tuple> filter)  throws InterruptedException { 
		// TODO Auto-generated method stub
		
		Optional<Tuple> result;
		
		// L'instruction au dessous est critique et bloquante (entouré implicitement par startRead et endRead)
		result = this.findOne(filter);
		
		// On vérifie optionnellement si il est présent (optionnel parce qu'on ne peut quitter le bloc critique
		// au dessus seulement si il existe un tuple qui correspand au filtre)
		if(result.isPresent()) {
			
			// On va considérer que la suppression est une opération d'ecriture
			//monitor.startWriting();
			this.remove(result.get());
			//monitor.endWriting();
		}
		
		return result;
	}

	@Override
	public Optional<Tuple> takeOne(Tuple motif) throws InterruptedException {
		Optional<Tuple> result;
		
		System.out.println("takeOne");
		
		// L'instruction au dessous est critique et bloquante (entouré implicitement par startRead et endRead)
		result = this.findOne(motif);
		
		// On vérifie optionnellement si il est présent (optionnel parce qu'on ne peut quitter le bloc critique
		// au dessus seulement si il existe un tuple qui correspand au motif)
		if(result.isPresent()) {
			
			// On va considérer que la suppression est une opération d'ecriture
			//monitor.startWriting();
			this.remove(result.get());
			//monitor.endWriting();
		}
		
		return result;
	}

	@Override
	public void add(ITupleSpace tupleSpace) throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void add(Tuple... tuples) throws InterruptedException {
		monitor.startWriting();
		
		// On appele pas la fonction this.add(tuple) ici (celle defini au dessous) car cette derniére
		// appelera les opérations du monitor à chaque insertion, ce qui est peu efficace puisque
		// nous souhaitons écrire des tuples en lot. Il vaut mieux ouvrir le mutex, inserer un lot de tuples,
		// puis refermer le mutex que de ouvrir le mutex, inserer un tuple, fermer le mutex et repeter ceci N fois !
		for(Tuple tuple : tuples) {
			this.tuples.add(tuple);
		}
		monitor.endWriting();
	}

	@Override
	public void add(Tuple tuple) throws InterruptedException {
		monitor.startWriting();
		tuples.add(tuple);
		monitor.endWriting();
	}
	
	// Permet d'utiliser un TupleSpace dans une boucle for directement [ for(Tuple tuple : tupleSpace) ]
	@Override
	public Iterator<Tuple> iterator() {
		return tuples.iterator();
	}
	
	@Override
	public boolean remove(Tuple tuple) throws InterruptedException {
		boolean exists = false;
		
		monitor.startWriting();
		exists = this.tuples.remove(tuple);
		monitor.endWriting();
		
		if(exists)
			System.out.println("[Remove] " + tuple);
		
		return exists;
	}
	
	@Override
	public void print() {
		System.out.println("___________________");
		/*for(Tuple tuple : tuples) {
			System.out.println(tuple);
		}
		*/
		System.out.println("___________________");
	}

	private boolean filter (Tuple t, Tuple motif) {
		if (t == null){
			return false;
		}
		return t.matches(motif);
	}

}
