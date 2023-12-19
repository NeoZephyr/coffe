package compile.antlr.script.memory;

public class BreakObject {

    private static BreakObject instance = new BreakObject();

    private BreakObject() {}

    public static BreakObject instance() {
        return instance;
    }

    @Override
    public String toString() {
        return "Break";
    }
}