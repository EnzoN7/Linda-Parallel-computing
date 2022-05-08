package linda.debug.interpreter;

public class LindaCommand {

    protected LindaOperation operation;

    public LindaCommand(LindaOperation operation) {
        this.operation = operation;
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


}
