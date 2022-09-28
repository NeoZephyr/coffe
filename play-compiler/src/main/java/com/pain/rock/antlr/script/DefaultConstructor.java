package com.pain.rock.antlr.script;


import com.pain.rock.antlr.script.symbol.Function;
import com.pain.rock.antlr.script.symbol.Klass;

public class DefaultConstructor extends Function {

    public DefaultConstructor(String name, Klass klass) {
        super(name, klass, null);
    }

    public Klass klass() {
        return (Klass) enclosingScope;
    }
}