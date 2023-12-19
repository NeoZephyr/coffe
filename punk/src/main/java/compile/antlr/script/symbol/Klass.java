package compile.antlr.script.symbol;

import compile.antlr.script.types.Type;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class Klass extends Scope implements Type {

    // 最顶层的基类
    private static final Klass ROOT_KLASS = new Klass("Object", null);

    // 父类
    Klass parent = null;

    This thisRef = null;

    Super superRef = null;

    DefaultConstructor defaultConstructor = null;

    public Klass(String name, ParserRuleContext ctx) {
        this.name = name;
        this.ctx = ctx;

        thisRef = new This(this, ctx);
        thisRef.type = this;
    }

    public void setParent(Klass parent) {
        this.parent = parent;

        // superRef 引用的也是自己
        superRef = new Super(parent, ctx);
        superRef.type = parent;
    }

    public Variable getVariable(String name) {
        Variable symbol = super.getVariable(name);

        if ((symbol == null) && (parent != null)) {
            symbol = parent.getVariable(name);
        }

        return symbol;
    }

    public Variable getFuncVariable(String name, List<Type> paramTypes) {
        Variable symbol = super.getFuncVariable(name, paramTypes);

        if ((symbol == null) && (parent != null)) {
            symbol = parent.getFuncVariable(name, paramTypes);
        }

        return symbol;
    }

    public Klass getKlass(String name) {
        Klass symbol = super.getKlass(name);

        if ((symbol == null) && (parent != null)) {
            symbol = parent.getKlass(name);
        }

        return symbol;
    }

    public Function getFunction(String name, List<Type> paramTypes) {
        Function func = super.getFunction(name, paramTypes);

        // 如果在本级找不到，那么递归的从父类中查找
        if ((func == null) && (parent != null)) {
            func = parent.getFunction(name, paramTypes);
        }

        return func;
    }

    public Function findConstructor(List<Type> paramTypes) {
        return super.getFunction(name, paramTypes);
    }

    public DefaultConstructor defaultConstructor() {
        if (defaultConstructor == null) {
            defaultConstructor = new DefaultConstructor(name, this);
        }

        return defaultConstructor;
    }

    /**
     * 是否包含某个 Symbol，这时候要把父类的成员考虑进来
     */
    public boolean containsSymbol(Symbol symbol) {
        if (symbol == thisRef || symbol == superRef) {
            return true;
        }

        boolean found = symbols.contains(symbol);

        if (!found && (parent != null)) {
            found = parent.containsSymbol(symbol);
        }

        return found;
    }

    /**
     * 本类型是不是另一个类型的祖先类型
     */
    public boolean isAncestor(Klass klass) {
        if (klass.parent != null) {
            if (klass.parent == this) {
                return true;
            } else {
                return isAncestor(klass.parent);
            }
        }

        return false;
    }

    /**
     * 当自身是目标类型的子类型的时候，返回 true
     */
    @Override
    public boolean isType(Type type) {
        if (this == type) {
            return true;
        }

        if (type instanceof Klass) {
            return ((Klass) type).isAncestor(this);
        }
        return false;
    }

    @Override
    public Scope getEnclosingScope() {
        return enclosingScope;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Class " + name;
    }
}