package linda.debug;

import linda.Tuple;
import linda.shm.CentralizedLinda;

import java.util.List;

public class DebugCentralizedLinda extends CentralizedLinda implements DebugLinda {

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


    static public int getTupleSize(Tuple tuple) {
        int size = 0;

        for(var element : tuple) {

        }

        return size;
    }
}
