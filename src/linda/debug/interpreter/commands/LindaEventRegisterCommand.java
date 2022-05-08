package linda.debug.interpreter.commands;

import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.debug.interpreter.LindaCommand;
import linda.debug.interpreter.LindaOperation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class LindaEventRegisterCommand extends LindaCommand {

    protected Class<?> callbackClass;
    protected Linda.eventMode mode;
    protected Linda.eventTiming timing;
    protected Tuple template;

    public LindaEventRegisterCommand(LindaOperation operation, Linda.eventMode mode, Linda.eventTiming timing, Tuple template, String callbackClassName) {
        super(operation);
        this.mode = mode;
        this.timing = timing;
        this.template = template;
        try {
            this.callbackClass = this.findClassNyName(callbackClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?> findClassNyName(String name) throws ClassNotFoundException {
        Class<?> cbClass = Class.forName(name);

        if(cbClass.isInterface()) {
            throw new RuntimeException("This class is an interface, it must be an implemented callback.");
        }

        if(Callback.class.isAssignableFrom(cbClass)) {
            return cbClass;
        } else {
            throw new RuntimeException("This class is not a callback (must implements Callback)");
        }

    }

    public Class<?> getCallbackClass() {
        return callbackClass;
    }

    public Linda.eventMode getMode() {
        return mode;
    }

    public Linda.eventTiming getTiming() {
        return timing;
    }


    public Tuple getTemplate() {
        return template;
    }

    public Callback createCallbackInstance() {
        try {
            Constructor<?> defaultConstructor = this.callbackClass.getConstructor();

            Callback callback = (Callback)defaultConstructor.newInstance();

            return callback;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
