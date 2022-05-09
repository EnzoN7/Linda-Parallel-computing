package linda;

import linda.cache.LindaCacheObserver;
import linda.evaluator.LindaEvaluator;

public interface LindaObservable {

    void setEvaluator(LindaEvaluator observer);

    void unsetEvaluator();

    boolean isEvaluated();

    void setCacheObserver(LindaCacheObserver observer);

    void unsetCacheObserver();

    boolean isObservedByCacheObserver();
}
