package linda.interpreter.commands;

import linda.interpreter.commands.parameters.LindaCacheOperation;
import linda.interpreter.LindaCommand;
import linda.interpreter.LindaOperation;

public class LindaCacheCommand extends LindaCommand {
    private LindaCacheOperation subOperation;
    public LindaCacheCommand(LindaOperation operation, LindaCacheOperation subOperation) {
        super(operation);

        this.subOperation = subOperation;
    }

    public LindaCacheOperation getSubOperation() {
        return subOperation;
    }
}
