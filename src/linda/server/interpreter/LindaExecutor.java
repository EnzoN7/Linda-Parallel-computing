package linda.server.interpreter;

import linda.server.LindaClient;
import linda.server.parser.LindaOperation;

public class LindaExecutor {

    private LindaClient client;

    public LindaExecutor(LindaClient client) {
        this.client = client;
    }

    public void execute(LindaFileCommand command) {
        if(command.is(LindaOperation.READ) ) {
            client.read(command.getTuple());
        } else if(command.is(LindaOperation.TAKE) ) {
            client.take(command.getTuple());
        } else if(command.is(LindaOperation.WRITE) ) {
            client.write(command.getTuple());
        } else {
            throw new RuntimeException("Incorrect operation");
        }
    }

    public void execute(LindaFileCommand... commands) {
        for(LindaFileCommand command : commands) {
            this.execute(command);
        }
    }
}
