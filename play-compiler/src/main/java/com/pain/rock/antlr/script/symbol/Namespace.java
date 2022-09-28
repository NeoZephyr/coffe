package com.pain.rock.antlr.script.symbol;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Namespace extends Block {
    private Namespace parent = null;
    private List<Namespace> children = new ArrayList<>();
    private String name;

    public Namespace(String name, Scope scope, ParserRuleContext ctx) {
        this.name = name;
        this.enclosingScope = scope;
        this.ctx = ctx;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<Namespace> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void addChild(Namespace child) {
        child.parent = this;
        children.add(child);
    }

    public void removeChild(Namespace child) {
        child.parent = null;
        children.remove(child);
    }
}