package compile.antlr.script.types;

import compile.antlr.script.symbol.Scope;

public class VoidType implements Type {

    private static final VoidType voidType = new VoidType();

    public static VoidType instance() {
        return voidType;
    }

    private VoidType() {}

    @Override
    public boolean isType(Type type) {
        return this == type;
    }

    @Override
    public Scope getEnclosingScope() {
        return null;
    }

    @Override
    public String getName() {
        return "void";
    }

    @Override
    public String toString() {
        return "void";
    }
}