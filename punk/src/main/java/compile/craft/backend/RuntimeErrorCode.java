package compile.craft.backend;

public enum RuntimeErrorCode {

    UNINITIALIZED_VALUE("Uninitialized value"),
    VALUE_RANGE("Value out of range"),
    INVALID_CASE_EXPRESSION_VALUE("Invalid CASE expression value"),
    DIVISION_BY_ZERO("Division by zero"),
    INVALID_STANDARD_FUNCTION_ARGUMENT("Invalid standard function argument"),
    INVALID_INPUT("Invalid input"),
    STACK_OVERFLOW("Runtime stack overflow"),
    UNIMPLEMENTED_FEATURE("Unimplemented runtime feature");

    public int code = 0;
    public final String message;

    RuntimeErrorCode(String message) {
        this.message = message;
    }

    RuntimeErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
