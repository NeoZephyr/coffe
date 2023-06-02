package jubi.netty.util;

import jubi.JubiException;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.net.BindException;
import java.util.function.Consumer;

@Slf4j
public class Utils {

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            log.error("IOException should not have been thrown.", e);
        }
    }

    public static void startService(int startPort, Consumer<Integer> starter) throws Exception {
        if ((startPort != 0 && startPort < 1024) || startPort > 65535) {
            throw new IllegalArgumentException("startPort should be between 1024 and 65535 (inclusive), or 0 for a random free port.");
        }

        int maxRetries = 3;

        for (int i = 0; i < maxRetries; i++) {
            int tryPort = startPort;

            if (startPort != 0) {
                tryPort = nextPort(startPort, i);
            }

            try {
                starter.accept(tryPort);
            } catch (Exception e) {
                if (isBindCollision(e)) {
                    if (i >= maxRetries - 1) {
                        Exception exception = new BindException(String.format("%d reties bind port", maxRetries));
                        e.setStackTrace(e.getStackTrace());
                        throw exception;
                    }

                    if (startPort == 0) {
                        log.warn("Could not bind on a random free port. You may check whether configuring an appropriate binding address.");
                    } else {
                        log.warn("could not bind on port {}. Attempting port {}.", tryPort, tryPort + 1);
                    }
                }

                throw new JubiException(String.format("Failed to start service$serviceString on port %d", startPort));
            }
        }
    }

    private static int nextPort(int base, int offset) {
        return (base + offset - 1024) % (65535 - 1024) + 1024;
    }

    private static boolean isBindCollision(Throwable e) {
        if (e instanceof BindException) {
            if (e.getMessage() != null) {
                return true;
            }

            return isBindCollision(e.getCause());
        }

        if (e instanceof Exception) {
            return isBindCollision(e.getCause());
        }

        return false;
    }
}
