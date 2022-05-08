package linda.debug.evaluator;

import linda.Tuple;

import java.util.Map;
import java.util.UUID;

public interface LindaObserver {

    Map<UUID, LindaEvaluation> getHistory();

    public void onWriteStart(UUID requestId, Tuple t);

    public void onWriteEnd(UUID requestId, Tuple t);

    public void onReadStart(UUID requestId, Tuple t);

    public void onReadEnd(UUID requestId, Tuple t);

    public void onTakeStart(UUID requestId, Tuple t);

    public void onTakeEnd(UUID requestId, Tuple t);
}
