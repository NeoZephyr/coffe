package compile.antlr.script.symbol;

import org.antlr.v4.runtime.ParserRuleContext;

public class This extends Variable {

    public This(Scope enclosingScope, ParserRuleContext ctx) {
        super("this", enclosingScope, ctx);
    }

    public Klass klass() {
        return (Klass) enclosingScope;
    }
}
