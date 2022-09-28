package com.pain.rock.antlr.script.symbol;

import com.pain.rock.antlr.script.*;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class Klass extends Scope implements Type {

    private Klass parentKlass;

    private This thisRef;

    private Super superRef;

    private DefaultConstructor defaultConstructor;

    // 最顶层的基类
    private static Klass rootKlass = new Klass("Object", null);

    public Klass(String name, ParserRuleContext ctx) {
        this.name = name;
        this.ctx = ctx;
        thisRef = new This(this, ctx);
        thisRef.type = this;
    }

    public Klass getParentKlass() {
        return parentKlass;
    }

    public DefaultConstructor getDefaultConstructor() {
        return defaultConstructor;
    }

    public void setParentKlass(Klass klass) {
        parentKlass = klass;
        superRef = new Super(parentKlass, ctx);
        superRef.type = parentKlass;
    }

    public This getThis() {
        return thisRef;
    }

    public Super getSuper() {
        return superRef;
    }

    @Override
    public Variable getVariable(String name) {
        Variable v = super.getVariable(name);

        if (v == null && parentKlass != null) {
            // TODO check visibility
            v = parentKlass.getVariable(name);
        }

        return v;
    }

    @Override
    public Klass getKlass(String name) {
        Klass c = super.getKlass(name);

        if (c == null && parentKlass != null) {
            // TODO check visibility
            c = parentKlass.getKlass(name);
        }

        return c;
    }

    /**
     * 只需要在本级查找
     */
    public Function findConstructor(List<Type> paramTypes) {
        // TODO check visibility
        return super.getFunction(name, paramTypes);
    }

    public Function getFunction(String name, List<Type> paramTypes) {
        Function func = super.getFunction(name, paramTypes);

        if (func == null && parentKlass != null) {
            // TODO check visibility
            func = parentKlass.getFunction(name, paramTypes);
        }

        return func;
    }

    public Variable getFunctionVariable(String name, List<Type> paramTypes) {
        Variable v = super.getFunctionVariable(name, paramTypes);

        if (v == null && parentKlass != null) {
            v = parentKlass.getFunctionVariable(name, paramTypes);
        }

        return v;
    }

    @Override
    protected boolean containsSymbol(Symbol symbol) {
        if (symbol == thisRef || symbol == superRef) {
            return true;
        }

        boolean flag = symbols.contains(symbol);

        if (!flag && parentKlass != null) {
            flag = parentKlass.containsSymbol(symbol);
        }

        return flag;
    }

    public boolean isType(Type type) {
        if (this == type) {
            return true;
        }

        if (type instanceof Klass) {
            return ((Klass) type).isAncestor(this);
        }

        return false;
    }

    public boolean isAncestor(Klass klass) {
        if (klass.getParentKlass() != null) {
            if (klass.getParentKlass() == this) {
                return true;
            } else {
                return isAncestor(klass.getParentKlass());
            }
        }

        return false;
    }

    public DefaultConstructor defaultConstructor() {
        if (defaultConstructor == null) {
            defaultConstructor = new DefaultConstructor(name, this);
        }

        return defaultConstructor;
    }

    @Override
    public String toString() {
        return "Class " + name;
    }
}