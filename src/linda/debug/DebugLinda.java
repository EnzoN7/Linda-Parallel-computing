package linda.debug;

import linda.Linda;
import linda.Tuple;

import java.util.List;

public interface DebugLinda extends Linda {

    List<Tuple> getTuples();

    long getTupleSpaceSize();

    int getTuplesNumber();

    long getFreeMemory();

    long getTotalMemory();

    long getInUseMemory();

    long getMaxMemory();

    int getAvailableProcessors();
}
