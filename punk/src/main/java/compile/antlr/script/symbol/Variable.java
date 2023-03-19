package compile.antlr.script.symbol;

import org.antlr.v4.runtime.ParserRuleContext;

public class Variable extends Symbol {
    public Type type;
    public Object defaultValue;
    public int multiplicity = 1;

    public Variable(String name, Scope scope, ParserRuleContext ctx) {
        this.name = name;
        this.enclosingScope = scope;
        this.ctx = ctx;
    }

    /**
     * 是否为类方法
     */
    public boolean isMember() {
        return enclosingScope instanceof Klass;
    }

    @Override
    public String toString() {
        return "Variable " + name + " -> "+ type;
    }
}
