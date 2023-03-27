package jubi.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;
import sun.misc.Signal;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SignalRegister {

    private static Map<String, ActionHandler> handlers = new HashMap<>();

    @FunctionalInterface
    interface Action {
        boolean exec();
    }

    public static void register(String sig, Action action) {
        if (!SystemUtils.IS_OS_UNIX) {
            log.info("===== only unix os support register signal");
            return;
        }

        Signal signal = new Signal(sig);
        ActionHandler handler = handlers.computeIfAbsent(sig, k -> new ActionHandler(signal));
        handler.register(action);
    }

    public static void main(String[] args) throws InterruptedException {
        // jps -lm
        // kill -2 <pid>
        register("INT", () -> {
            log.info("action1");
            return false;
        });
        register("INT", () -> {
            log.info("action2");
            return false;
        });

        Thread.sleep(1000 * 1000);
    }
}
