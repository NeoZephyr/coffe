package compile.antlr.script.symbol;

public class DefaultConstructor extends Function {

    public DefaultConstructor(String name, Scope enclosingScope) {
        super(name, enclosingScope, null);
    }

    public Klass klass() {
        return (Klass) enclosingScope;
    }
}
