package compile.antlr.script.symbol;

import compile.antlr.script.types.Type;
import org.antlr.v4.runtime.ParserRuleContext;

public class Variable extends Symbol {

    // 变量类型
    public Type type = null;

    // 缺省值
    private Object defaultValue = null;

    // 是否允许多次重复，这是一个创新的参数机制
    private Integer multiplicity = 1;

    public Variable(String name, Scope enclosingScope, ParserRuleContext ctx) {
        this.name = name;
        this.enclosingScope = enclosingScope;
        this.ctx = ctx;
    }

    /**
     * 是不是类的属性
     */
    public boolean isKlassMember() {
        return enclosingScope instanceof Klass;
    }

    @Override
    public String toString() {
        return "Variable " + name + " -> "+ type;
    }
}