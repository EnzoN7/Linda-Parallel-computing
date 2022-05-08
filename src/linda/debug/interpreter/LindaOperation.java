package linda.debug.interpreter;

public enum LindaOperation {
    READ,
    TAKE,
    WRITE,

    READ_ALL,

    TAKE_ALL,
    EVENT_REGISTER,

    DEBUG,

    MEMORY,

    HISTORY,

    STATS;


    public static LindaOperation getFromString(String _operation) {
        for(var operation : LindaOperation.values()) {
            if(_operation.equalsIgnoreCase(operation.name())) {
                return operation;
            }
        }

        return null;
    }
}
