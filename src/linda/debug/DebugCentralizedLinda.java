package linda.debug;

import linda.Tuple;
import linda.debug.evaluator.LindaEvaluation;
import linda.debug.evaluator.LindaObservable;
import linda.debug.evaluator.LindaObserver;
import linda.shm.CentralizedLinda;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DebugCentralizedLinda extends CentralizedLinda implements DebugLinda, LindaObservable {

    LindaObserver mainObserver;

    @Override
    public void write(Tuple t) {
        UUID reqId = UUID.randomUUID();

        if(this.isObserved()) mainObserver.onWriteStart(reqId, t);
        super.write(t);
        if(this.isObserved()) mainObserver.onWriteEnd(reqId, t);
    }

    @Override
    public Tuple read(Tuple t) {
        UUID reqId = UUID.randomUUID();

        if(this.isObserved()) mainObserver.onReadStart(reqId, t);
        var result = super.read(t);
        if(this.isObserved()) mainObserver.onReadEnd(reqId, t);

        return result;
    }

    @Override
    public Tuple take(Tuple t) {
        UUID reqId = UUID.randomUUID();

        if(this.isObserved()) mainObserver.onTakeStart(reqId, t);
        var result = super.take(t);
        if(this.isObserved()) mainObserver.onTakeEnd(reqId, t);

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
        return this.mainObserver.getHistory();
    }
    
    static public int getTupleSize(Tuple tuple) {
        int size = 0;

        for(var element : tuple) {

        }

        return size;
    }

    @Override
    public void setObserver(LindaObserver observer) {
        mainObserver = observer;
    }

    @Override
    public void unsetObserver() {
        mainObserver = null;
    }

    @Override
    public boolean isObserved() {
        return mainObserver != null;
    }
}
