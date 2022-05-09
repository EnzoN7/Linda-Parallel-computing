package linda.interpreter.commands;

import linda.interpreter.LindaCommand;
import linda.interpreter.commands.parameters.LindaHistoryType;
import linda.interpreter.LindaOperation;

public class LindaHistoryCommand extends LindaCommand {

    private LindaHistoryType type;

    public LindaHistoryCommand(LindaOperation operation, LindaHistoryType type) {
        super(operation);

        this.type = type;
    }

    public LindaHistoryType getType() {
        return type;
    }

    public void setType(LindaHistoryType type) {
        this.type = type;
    }

    public boolean isLocal() {
        return this.type.equals(LindaHistoryType.LOCAL);
    }

    public boolean isRemote() {
        return this.type.equals(LindaHistoryType.REMOTE);
    }
}
