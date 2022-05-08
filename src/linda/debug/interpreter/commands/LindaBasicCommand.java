package linda.debug.interpreter.commands;

import linda.Tuple;
import linda.debug.interpreter.LindaCommand;
import linda.debug.interpreter.LindaOperation;

public class LindaBasicCommand extends LindaCommand {
    protected Tuple tuple;

    public LindaBasicCommand(LindaOperation operation, Tuple tuple) {
        super(operation);
        this.tuple = tuple;
    }

    public Tuple getTuple() {
        return tuple;
    }

    public void setTuple(Tuple tuple) {
        this.tuple = tuple;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(operation.toString());
        builder.append(" ");
        builder.append(tuple.toString());

        return builder.toString();
    }
}
