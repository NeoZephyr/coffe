package compile.antlr.script.symbol;

import org.antlr.v4.runtime.ParserRuleContext;

public class Block extends Scope {

    private static int index = 1;

    public Block() {
        this.name = "block" + index++;
    }

    public Block(Scope enclosingScope, ParserRuleContext ctx) {
        this.name = "block" + index++;
        this.enclosingScope = enclosingScope;
        this.ctx = ctx;
    }

    @Override
    public String toString() {
        return "Block " + name;
    }
}