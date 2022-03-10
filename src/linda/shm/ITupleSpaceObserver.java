package linda.shm;

import linda.Tuple;

public interface ITupleSpaceObserver {

	// Methode appel�e par l'observateur � chaque fois qu'un tuple est ajout� au TupleSpace
	// Le CallbackManager reimplementera cette methode de sorte � v�rifier si le motif du tuple correspand
	// � un des motifs enrigstr�s, si c'est le cas il appelera le callback correspandant.
	void onWrite(Tuple tuple) throws InterruptedException;
	
	void onRead(Tuple tuple);
	
	void onTake(Tuple tuple);
}
