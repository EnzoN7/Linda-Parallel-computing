package linda.shm;

import java.util.Collection;
import java.util.concurrent.*;

import linda.AsynchronousCallback;
import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.shm.callables.LindaCallable;
import linda.shm.callables.ReadCallable;
import linda.shm.callables.RegisterCallable;
import linda.shm.callables.TakeCallable;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {
	
	private ILindaMonitor monitor;

	private ITupleSpace tupleSpace;
	
	private ExecutorService executorService;
	
	private ICallbackManager callbackManager;
	
	public CentralizedLinda() {
    	tupleSpace = new TupleSpace();
    	callbackManager = new CallbackManager(this);
    	monitor = new LindaMonitor(tupleSpace, callbackManager);
    	
    	this.executorService = Executors.newFixedThreadPool(10000);
	}
	
    public CentralizedLinda(ExecutorService executorService) {
    	this();
    	
    	this.executorService = executorService;
    }
    
	@Override
	public void write(Tuple t) {

		if (t != null){
			try {
				// L'acces à TupleSpace est concurrent et géré par un moniteur suivant le modéle Readers/Writer (voir TupleSpaceMonitor)
				// Ce qui signifie qu'on ne peut écrire (ajouter un tuple au TupleSpace) seulement si :
				// Il n'y a aucune ecriture en cours
				// Il n'y a aucune lecture en cours
				// Il n'y a aucun thread-lecteur en attente de lire
				tupleSpace.add(t);

				// Lorsque les trois conditions précedentes sont vérifiés, le thread est reveillé
				// et on affichera à un message comme quoi on a réussi à ajouter le tuple à l'espace
				System.out.println("Successful WRITE : " + t);


				// Puis, on lancera une callback qui permettra de reveiller, si il existe, un thread qui a lancé
				// une operation Linda.read(motif) mais qui est actuellement bloqué car il n'existe pas un tuple
				// qui correspand au motif, si le tuple qu'on vient tout juste d'ajouter à l'espace correspand
				// au motif surlequelle un thread attend de lire actuellement, alors il sera reveillé.
				// Ceci est géré par un deuxieme moniteur nommé LindaMonitor.
				monitor.onWritingEnd(t);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}

	@Override
	public Tuple take(Tuple template) {
		TakeCallable takeRunnable = new TakeCallable(monitor, template, false, false);
		
		System.out.println("Submit: TAKE " + template.toString());
		
		/* 	* On lancera une nouvelle tache de type read qui a comme but de trouver 
			* dans le TupleSpace un tuple qui correspand au motif (template) donnée comme parametre,
			* si c'est trouvé alors la tache sera realisé et on retournera le tuple comme sortie
			* de cette fonction, dans le cas contraire, le thread sera bloqué en attendant une existence
			* future d'un tuple qui correspand au motif. En pratique, ce thread ne sera reveillé que par
			* une opération d'écriture write(tuple) définie dans cette classe.
		*/ 
		Future<Collection<Tuple>> future = executorService.submit(takeRunnable);
		try {
			// Une fois que la tache est réalisé, on retournera la valeur.
			return future.get().stream().findAny().get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public Tuple read(Tuple template) {
		ReadCallable readRunnable = new ReadCallable(monitor, template, false, false);
			
		System.out.println("Submit: Read ( " + template.toString() + " )");
			
		/* On lancera une nouvelle tache de type read qui a comme but de trouver 
			* dans le TupleSpace un tuple qui correspand au motif (template) donnée comme parametre,
			* si c'est trouvé alors la tache sera realisé et on retournera le tuple comme sortie
			* de cette fonction, dans le cas contraire, le thread sera bloqué en attendant une existence
			* future d'un tuple qui correspand au motif. En pratique, ce thread ne sera reveillé que par
			* une opération d'écriture write(tuple) définie dans cette classe.
		*/ 
		Future<Collection<Tuple>> future = executorService.submit(readRunnable);
			
		try {
			// Une fois que la tache est réalisé, on retournera la valeur.
			return future.get().stream().findAny().get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public Tuple tryTake(Tuple template) {
		TakeCallable takeRunnable = new TakeCallable(monitor, template, true, false);

		System.out.println("Submit: TryTake( " + template.toString() + " )");

		/* 	* On lancera une nouvelle tache de type read qui a comme but de trouver
		 * dans le TupleSpace un tuple qui correspand au motif (template) donnée comme parametre,
		 * si c'est trouvé alors la tache sera realisé et on retournera le tuple comme sortie
		 * de cette fonction, dans le cas contraire, le thread sera bloqué en attendant une existence
		 * future d'un tuple qui correspand au motif. En pratique, ce thread ne sera reveillé que par
		 * une opération d'écriture write(tuple) définie dans cette classe.
		 */
		Future<Collection<Tuple>> future = executorService.submit(takeRunnable);
		try {
			// Une fois que la tache est réalisé, on retournera la valeur.
			Collection<Tuple> result = future.get();
			if (result != null) {
				return result.stream().findAny().get();
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Tuple tryRead(Tuple template) {
		ReadCallable readRunnable = new ReadCallable(monitor, template, true, false);

		System.out.println("Submit: READ " + template.toString());

		/* On lancera une nouvelle tache de type read qui a comme but de trouver
		 * dans le TupleSpace un tuple qui correspand au motif (template) donnée comme parametre,
		 * si c'est trouvé alors la tache sera realisé et on retournera le tuple comme sortie
		 * de cette fonction, dans le cas contraire, le thread sera bloqué en attendant une existence
		 * future d'un tuple qui correspand au motif. En pratique, ce thread ne sera reveillé que par
		 * une opération d'écriture write(tuple) définie dans cette classe.
		 */
		Future<Collection<Tuple>> future = executorService.submit(readRunnable);

		try {
			// Une fois que la tache est réalisé, on retournera la valeur.
			Collection<Tuple> result = future.get();
			if (result != null) {
				return result.stream().findAny().get();
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Collection<Tuple> takeAll(Tuple template) {
		TakeCallable takeRunnable = new TakeCallable(monitor, template, false, true);

		System.out.println("Submit: TAKEALL " + template.toString());

		/* 	* On lancera une nouvelle tache de type read qui a comme but de trouver
		 * dans le TupleSpace un tuple qui correspand au motif (template) donnée comme parametre,
		 * si c'est trouvé alors la tache sera realisé et on retournera le tuple comme sortie
		 * de cette fonction, dans le cas contraire, le thread sera bloqué en attendant une existence
		 * future d'un tuple qui correspand au motif. En pratique, ce thread ne sera reveillé que par
		 * une opération d'écriture write(tuple) définie dans cette classe.
		 */
		Future<Collection<Tuple>> future = executorService.submit(takeRunnable);
		try {
			// Une fois que la tache est réalisé, on retournera la valeur.
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Collection<Tuple> readAll(Tuple template) {
		ReadCallable readRunnable = new ReadCallable(monitor, template, false, true);

		System.out.println("Submit: READALL " + template.toString());

		/* On lancera une nouvelle tache de type read qui a comme but de trouver
		 * dans le TupleSpace un tuple qui correspand au motif (template) donnée comme parametre,
		 * si c'est trouvé alors la tache sera realisé et on retournera le tuple comme sortie
		 * de cette fonction, dans le cas contraire, le thread sera bloqué en attendant une existence
		 * future d'un tuple qui correspand au motif. En pratique, ce thread ne sera reveillé que par
		 * une opération d'écriture write(tuple) définie dans cette classe.
		 */
		Future<Collection<Tuple>> future = executorService.submit(readRunnable);

		try {
			// Une fois que la tache est réalisé, on retournera la valeur.
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {

		System.out.println("eventRegister : " + mode + " , " + timing + ", " + template);
		if(timing == eventTiming.FUTURE) {
			if(mode == eventMode.READ)
				callbackManager.registerReadCallback(template, callback);
			else if(mode == eventMode.TAKE) 
				callbackManager.registerTakeCallback(template, callback);
		} else if(timing == eventTiming.IMMEDIATE) {
			if(mode == eventMode.READ) {
				Tuple result = tryRead(template);
				if (result != null){
					RegisterCallable registerCallable = new RegisterCallable(new AsynchronousCallback(callback), result);
					executorService.submit(registerCallable);
				} else {
					callbackManager.registerReadCallback(template, callback);
				}
			}
			else if(mode == eventMode.TAKE) {
				System.out.println("[EventRegister] mode == eventMode.TAKE");
				tupleSpace.print();
				Tuple result = tryTake(template);
				if (result != null){
					System.out.println("[EventRegister] Tuple existe, lançons IMMEDIATE");
					RegisterCallable registerCallable = new RegisterCallable(callback, result);//new AsynchronousCallback(callback), result);
					//callback.call(result);
					executorService.submit(registerCallable);
				} else {
					System.out.println("[EventRegister] Aucun tuple trouvé, lançons FUTURE");
					callbackManager.registerTakeCallback(template, callback);
				}
			}
		}
		
	}

	@Override
	public void debug(String prefix) {
		// TODO 
		
	}

    // TO BE COMPLETED

}
