package linda;

import linda.evaluator.LindaEvaluation;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface LindaObserver {

    Map<UUID, LindaEvaluation> getHistory();

    void onWriteStart(UUID requestId, Tuple t);

    void onWriteEnd(UUID requestId, Tuple t);

    Optional<Tuple> onReadStart(UUID requestId, Tuple t);

    void onReadEnd(UUID requestId, Tuple template, Tuple result);

    void onTakeStart(UUID requestId, Tuple t);

    void onTakeEnd(UUID requestId, Tuple t, Tuple result);
}
