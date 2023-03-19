package compile.antlr.script;

import compile.antlr.script.symbol.Function;
import compile.antlr.script.symbol.Klass;

public class DefaultConstructor extends Function {

    public DefaultConstructor(String name, Klass klass) {
        super(name, klass, null);
    }

    public Klass klass() {
        return (Klass) enclosingScope;
    }
}