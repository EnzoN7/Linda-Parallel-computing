package linda.interpreter.commands;

import linda.interpreter.LindaCommand;
import linda.interpreter.LindaOperation;
import linda.interpreter.commands.parameters.LindaStatsOperation;

public class LindaStatsCommand extends LindaCommand {

    private LindaStatsOperation subOperation;

    public LindaStatsCommand(LindaOperation operation, LindaStatsOperation subOperation) {
        super(operation);

        this.subOperation = subOperation;
    }

    public LindaStatsOperation getSubOperation() {
        return subOperation;
    }
}
