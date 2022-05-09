package linda.evaluator;

import linda.Tuple;
import linda.interpreter.LindaOperation;

import java.io.Serializable;

public class LindaEvaluation implements Serializable {
    public LindaEvaluation(LindaOperation operation, Tuple tuple, Long duration) {
        this.operation = operation;
        this.tuple = tuple;
        this.duration = duration;
    }

    public LindaOperation operation;
    public Long duration;

    public Tuple tuple;

    public boolean fromCache;
}
