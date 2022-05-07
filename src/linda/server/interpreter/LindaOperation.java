package linda.server.parser;

public enum LindaOperation {
    READ,
    TAKE,
    WRITE;

    public static LindaOperation getFromString(String _operation) {
        for(var operation : LindaOperation.values()) {
            if(_operation.equalsIgnoreCase(operation.name())) {
                return operation;
            }
        }

        return null;
    }
}
