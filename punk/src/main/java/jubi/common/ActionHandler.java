package jubi.common;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.ArrayList;
import java.util.List;

public class ActionHandler implements SignalHandler {

    private List<SignalRegister.Action> actions = new ArrayList<>();
    private SignalHandler prevHandler;

    public ActionHandler(Signal signal) {
        prevHandler = Signal.handle(signal, this);
    }

    @Override
    public void handle(Signal signal) {
        Signal.handle(signal, prevHandler);

        boolean escalate = true;

        for (SignalRegister.Action action : actions) {
            if (action.exec()) {
                escalate = false;
            }
        }

        if (escalate) {
            prevHandler.handle(signal);
        }

        Signal.handle(signal, this);
    }

    public void register(SignalRegister.Action action) {
        actions.add(action);
    }
}
