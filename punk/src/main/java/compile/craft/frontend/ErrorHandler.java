package compile.craft.frontend;

public class ErrorHandler {

    /**
     * Flag an error in the source line
     */
    public void flag(Token token, ErrorCode errorCode) {
        // send error message by message producer
    }

    public void abort(ErrorCode errorCode) {
        String fatalError = "FATAL ERROR: " + errorCode.toString();
        // send error message by message producer
        System.exit(errorCode.code);
    }
}
