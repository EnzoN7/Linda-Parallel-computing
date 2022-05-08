package linda.debug.evaluator;

public interface LindaObservable {

    void setObserver(LindaObserver observer);

    void unsetObserver();

    boolean isObserved();

}
