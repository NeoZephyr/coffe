package compile.antlr.script.symbol;

import org.antlr.v4.runtime.ParserRuleContext;

public class Super extends Variable {

    public Super(Scope enclosingScope, ParserRuleContext ctx) {
        super("super", enclosingScope, ctx);
    }

    public Klass klass() {
        return (Klass) enclosingScope;
    }
}
