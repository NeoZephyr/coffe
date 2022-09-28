package com.pain.rock.antlr.script.symbol;

import org.antlr.v4.runtime.ParserRuleContext;

public class Super extends Variable {
    public Super(Klass klass, ParserRuleContext ctx) {
        super("super", klass, ctx);
    }

    private Klass klass() {
        return (Klass) enclosingScope;
    }
}
