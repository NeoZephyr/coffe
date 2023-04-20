package jubi;

public class JubiException extends Exception {

    public JubiException(String message) {
        super(message);
    }

    public JubiException(String message, Throwable cause) {
        super(message, cause);
    }
}
