package com.pain.rock.antlr.script.symbol;

import org.antlr.v4.runtime.ParserRuleContext;

public abstract class Symbol {
    public String name;

    // 所属作用域
    protected Scope enclosingScope;

    // 可见性
    protected int visibility = 0;

    // Symbol 关联的 AST 节点
    public ParserRuleContext ctx;

    public String getName() {
        return name;
    }

    public Scope getEnclosingScope() {
        return enclosingScope;
    }

    public void setCtx(ParserRuleContext ctx) {
        this.ctx = ctx;
    }
}