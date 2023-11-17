package compile.antlr.script.symbol;

import compile.antlr.script.scope.Scope;

public class DefaultCtorSymbol extends FuncSymbol {

    public DefaultCtorSymbol(String name, Scope enclosingScope) {
        super(name, enclosingScope, null);
    }
}
