package compile.antlr.script.symbol;

public class VoidType implements Type {

    private static final VoidType instance = new VoidType();

    private VoidType() {}

    @Override
    public String getName() {
        return "void";
    }

    @Override
    public Scope getEnclosingScope() {
        return null;
    }

    @Override
    public boolean isType(Type type) {
        return this == type;
    }

    public static VoidType instance() {
        return instance;
    }

    @Override
    public String toString() {
        return "void";
    }
}
