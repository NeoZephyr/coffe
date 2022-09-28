package com.pain.rock.antlr.script.symbol;

public interface Type {
    String getName();

    Scope getEnclosingScope();

    boolean isType(Type type);
}