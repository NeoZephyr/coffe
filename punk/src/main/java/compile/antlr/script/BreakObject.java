package compile.antlr.script;

public class BreakObject {

    private static final BreakObject instance = new BreakObject();

    private BreakObject() {}

    public static BreakObject instance() {
        return instance;
    }

    @Override
    public String toString() {
        return "Break";
    }
}