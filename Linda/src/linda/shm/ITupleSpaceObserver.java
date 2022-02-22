package linda.shm;

import linda.Tuple;

public interface ITupleSpaceObserver {

	// Methode appelée par l'observateur à chaque fois qu'un tuple est ajouté au TupleSpace
	// Le CallbackManager reimplementera cette methode de sorte à vérifier si le motif du tuple correspand
	// à un des motifs enrigstrés, si c'est le cas il appelera le callback correspandant.
	void onWrite(Tuple tuple) throws InterruptedException;
	
	void onRead(Tuple tuple);
	
	void onTake(Tuple tuple);
}
