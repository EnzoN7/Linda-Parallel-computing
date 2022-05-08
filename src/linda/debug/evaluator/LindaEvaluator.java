package linda.debug.evaluator;

import linda.Tuple;
import linda.debug.interpreter.LindaOperation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LindaEvaluator implements  LindaObserver {

    private Map<UUID, LindaEvaluation> requestEvaluations;

    public LindaEvaluator() {
        requestEvaluations = new HashMap<>();
    }
    @Override
    public void onWriteStart(UUID requestId, Tuple t) {
        addRequest(requestId, LindaOperation.WRITE, t);
    }

    @Override
    public void onWriteEnd(UUID requestId, Tuple t) {
        long duration = evalRequest(requestId);

        System.out.println("[Performance] Writing " + t + " tooks " + duration + " ms.");

        //removeRequest(requestId);
    }

    private void addRequest(UUID requestId, LindaOperation operation, Tuple tuple) {
        requestEvaluations.put(requestId, new LindaEvaluation(operation, tuple, System.nanoTime()));
    }

    private long evalRequest(UUID requestId) {
        if(requestEvaluations.containsKey(requestId)) {
            LindaEvaluation evaluation = requestEvaluations.get(requestId);

            long startTime = evaluation.duration;
            long endTime = System.nanoTime();

            evaluation.duration = (endTime - startTime) ;

            return evaluation.duration;
        } else {
            throw new RuntimeException("Cannot evaluate a request that doesn't exists");
        }
    }

    @Override
    public Map<UUID, LindaEvaluation> getHistory() {
        return requestEvaluations;
    }

    @Override
    public void onReadStart(UUID requestId, Tuple t) {
        addRequest(requestId, LindaOperation.READ, t);
    }

    @Override
    public void onReadEnd(UUID requestId, Tuple t) {
        long duration = evalRequest(requestId);

        System.out.println("[Performance] Reading " + t + " tooks " + duration + " ms.");
    }

    @Override
    public void onTakeStart(UUID requestId, Tuple t) {
        addRequest(requestId, LindaOperation.TAKE, t);
    }

    @Override
    public void onTakeEnd(UUID requestId, Tuple t) {
        long duration = evalRequest(requestId);

        System.out.println("[Performance] Taking " + t + " tooks " + duration + " ms.");
    }
}
