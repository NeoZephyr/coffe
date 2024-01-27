package compile.craft.frontend;

public enum ErrorCode {
    IDENTIFIER_REDEFINED("Redefined identifier"),
    IDENTIFIER_UNDEFINED("Undefined identifier"),
    INVALID_CHARACTER("Invalid character"),
    UNCLOSED_CHARACTER("Unclosed character"),
    UNCLOSED_STRING("Unclosed string"),
    UNEXPECTED_EOF("Unexpected end of file"),
    UNEXPECTED_TOKEN("Unexpected token"),
    RANGE_DECIMAL(""),
    RANGE_FLOAT(""),
    IO_ERROR(-101, "Object I/O error"),
    TOO_MANY_ERRORS(-102, "Too many syntax errors");

    public int code = 0;
    public final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
