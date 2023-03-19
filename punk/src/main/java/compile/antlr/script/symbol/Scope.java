package compile.antlr.script.symbol;

import java.util.ArrayList;
import java.util.List;

public abstract class Scope extends Symbol {

    // 该 Scope 中的成员，包括变量、方法、类等
    protected List<Symbol> symbols = new ArrayList<>();

    public void addSymbol(Symbol symbol) {
        symbols.add(symbol);
        symbol.enclosingScope = this;
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public Variable getVariable(String name) {
        return getVariable(this, name);
    }

    public Function getFunction(String name, List<Type> paramTypes) {
        return getFunction(this, name, paramTypes);
    }

    public Variable getFunctionVariable(String name, List<Type> paramTypes) {
        return getFunctionVariable(this, name, paramTypes);
    }

    protected static Variable getFunctionVariable(Scope scope, String name, List<Type> paramTypes) {
        for (Symbol symbol : scope.symbols) {
            if (symbol instanceof Variable && ((Variable) symbol).type instanceof FunctionType && symbol.name.equals(name)) {
                Variable v = (Variable) symbol;
                FunctionType functionType = (FunctionType) v.type;

                if (functionType.matchParamTypes(paramTypes)) {
                    return v;
                }
            }
        }

        return null;
    }

    public Klass getKlass(String name) {
        return getKlass(this, name);
    }

    public boolean containsSymbol(Symbol symbol) {
        return symbols.contains(symbol);
    }

    protected boolean accessible(Symbol symbol) {
        boolean flag = containsSymbol(symbol);

        if (!flag && enclosingScope != null) {
            flag = enclosingScope.accessible(symbol);
        }

        return flag;
    }

    @Override
    public String toString() {
        return "Scope: " + name;
    }

    public static Variable getVariable(Scope scope, String name) {
        for (Symbol symbol : scope.symbols) {
            if (symbol instanceof Variable && symbol.name.equals(name)) {
                return (Variable) symbol;
            }
        }

        return null;
    }

    public static Function getFunction(Scope scope, String name, List<Type> paramTypes) {
        for (Symbol symbol : scope.symbols) {
            if (symbol instanceof Function && symbol.name.equals(name)) {
                Function function = (Function) symbol;

                if (function.matchParamTypes(paramTypes)) {
                    return function;
                }
            }
        }

        return null;
    }

    protected static Klass getKlass(Scope scope, String name) {
        for (Symbol symbol : scope.symbols) {
            if (symbol instanceof Klass && symbol.name.equals(name)) {
                return (Klass) symbol;
            }
        }

        return null;
    }
}