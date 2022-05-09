package linda.debug;

import linda.LindaObservable;
import linda.Tuple;
import linda.cache.LindaCacheObserver;
import linda.evaluator.LindaEvaluation;
import linda.evaluator.LindaEvaluator;
import linda.shm.CentralizedLinda;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DebugCentralizedLinda extends CentralizedLinda implements DebugLinda, LindaObservable {

    private LindaEvaluator evaluator;

    private LindaCacheObserver cacheObserver;

    @Override
    public void write(Tuple t) {
        UUID reqId = UUID.randomUUID();

        if(this.isEvaluated()) evaluator.onWriteStart(reqId, t);
        super.write(t);
        if(this.isEvaluated()) evaluator.onWriteEnd(reqId, t);
    }

    @Override
    public Tuple read(Tuple t) {
        UUID reqId = UUID.randomUUID();

        if(this.isEvaluated()) evaluator.onReadStart(reqId, t);
        /*if(this.isObservedByCacheObserver()) {
            Optional<Tuple> _tuple = cacheObserver.onReadStart(reqId, t);
            if(_tuple.isPresent()) {
                System.out.println("FROM CACHE");
                evaluator.setFromCache(reqId);
                return _tuple.get();
            }
        }*/
        var result = super.read(t);
        if(this.isEvaluated()) evaluator.onReadEnd(reqId, t, result);

        return result;
    }

    @Override
    public Tuple take(Tuple t) {
        UUID reqId = UUID.randomUUID();

        if(this.isEvaluated()) evaluator.onTakeStart(reqId, t);
        var result = super.take(t);
        if(this.isEvaluated()) evaluator.onTakeEnd(reqId, t, result);

        return result;
    }

    @Override
    public List<Tuple> getTuples() {
        return this.tupleSpace.getTuples();
    }

    @Override
    public long getTupleSpaceSize() {
        return this.tupleSpace.getTuples().size();
    }

    @Override
    public int getTuplesNumber() {
        return this.tupleSpace.getTuples().size();
    }

    @Override
    public long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    @Override
    public long getTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    @Override
    public long getInUseMemory() {
        return this.getTotalMemory() - this.getFreeMemory();
    }

    @Override
    public long getMaxMemory() {
        return Runtime.getRuntime().totalMemory();
    }

    @Override
    public int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    @Override
    public Map<UUID, LindaEvaluation> getHistory() {
        return this.evaluator.getHistory();
    }

    static public int getTupleSize(Tuple tuple) {
        int size = 0;

        for(var element : tuple) {

        }

        return size;
    }

    @Override
    public void setEvaluator(LindaEvaluator observer) {
        evaluator = observer;
    }

    @Override
    public void unsetEvaluator() {
        evaluator = null;
    }

    @Override
    public boolean isEvaluated() {
        return evaluator != null;
    }


    @Override
    public void setCacheObserver(LindaCacheObserver observer) {
        cacheObserver = observer;
    }

    @Override
    public void unsetCacheObserver() {
        cacheObserver = null;
    }

    @Override
    public boolean isObservedByCacheObserver() {
        return cacheObserver != null;
    }
}
