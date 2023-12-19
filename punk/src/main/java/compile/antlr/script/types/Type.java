package compile.antlr.script.types;

import compile.antlr.script.symbol.Scope;

public interface Type {

    boolean isType(Type type);

    Scope getEnclosingScope();

    String getName();
}
