package compile.antlr.script.symbol;

import compile.antlr.script.scope.Scope;
import org.antlr.v4.runtime.ParserRuleContext;

public class SuperSymbol extends VarSymbol {

    public SuperSymbol(Scope enclosingScope, ParserRuleContext ctx) {
        super("super", enclosingScope, ctx);
    }
}
