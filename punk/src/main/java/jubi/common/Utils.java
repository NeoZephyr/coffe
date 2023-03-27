package jubi.common;

import java.io.File;

public class Utils {

    public static int DEFAULT_SHUTDOWN_PRIORITY = 100;
    public static int SERVER_SHUTDOWN_PRIORITY = 75;

    public static void addShutdownHook(Runnable hook, int priority) {
        ShutdownHookManager.get().addShutdownHook(hook, priority);
    }

    public static void deleteDirectoryRecursively(File file) {
        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();

            for (File subFile : subFiles) {
                deleteDirectoryRecursively(subFile);
            }
        }
        file.delete();
    }
}
