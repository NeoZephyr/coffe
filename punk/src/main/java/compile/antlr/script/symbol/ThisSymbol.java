package compile.antlr.script.symbol;

import compile.antlr.script.scope.Scope;
import org.antlr.v4.runtime.ParserRuleContext;

public class ThisSymbol extends VarSymbol {

    public ThisSymbol(Scope enclosingScope, ParserRuleContext ctx) {
        super("this", enclosingScope, ctx);
    }
}
