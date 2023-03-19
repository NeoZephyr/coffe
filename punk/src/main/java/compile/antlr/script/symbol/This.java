package compile.antlr.script.symbol;

import org.antlr.v4.runtime.ParserRuleContext;

public class This extends Variable {

    public This(Klass klass, ParserRuleContext ctx) {
        super("this", klass, ctx);
    }

    private Klass klass() {
        return (Klass) enclosingScope;
    }
}
