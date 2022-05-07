package linda.server.interpreter;

public enum LindaOperation {
    READ,
    TAKE,
    WRITE,
    EVENT_REGISTER;

    public static LindaOperation getFromString(String _operation) {
        for(var operation : LindaOperation.values()) {
            if(_operation.equalsIgnoreCase(operation.name())) {
                return operation;
            }
        }

        return null;
    }
}
