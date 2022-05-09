package linda.debug;

import linda.Linda;
import linda.Tuple;
import linda.evaluator.LindaEvaluation;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DebugLinda extends Linda {

    List<Tuple> getTuples();

    long getTupleSpaceSize();

    int getTuplesNumber();

    long getFreeMemory();

    long getTotalMemory();

    long getInUseMemory();

    long getMaxMemory();

    int getAvailableProcessors();

    Map<UUID, LindaEvaluation> getHistory();
}
