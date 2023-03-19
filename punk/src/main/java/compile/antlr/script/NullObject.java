package compile.antlr.script;

public class NullObject extends KlassObject {
    private static final NullObject instance = new NullObject();

    private NullObject() {}

    public static NullObject instance() {
        return instance;
    }

    @Override
    public String toString() {
        return "Null";
    }
}