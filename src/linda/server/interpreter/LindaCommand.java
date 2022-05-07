package linda.server.file;

import linda.Tuple;
import linda.server.parser.LindaOperation;

public class LindaFileCommand {

    protected LindaOperation operation;

    protected Tuple tuple;

    public LindaFileCommand(LindaOperation operation, Tuple tuple) {
        this.operation = operation;
        this.tuple = tuple;
    }

    public boolean is(LindaOperation operation) {
        return this.operation.equals(operation);
    }

    public LindaOperation getOperation() {
        return operation;
    }

    public void setOperation(LindaOperation operation) {
        this.operation = operation;
    }

    public Tuple getTuple() {
        return tuple;
    }

    public void setTuple(Tuple tuple) {
        this.tuple = tuple;
    }
}
