package compile.antlr.script.symbol;

import compile.antlr.script.types.FunctionType;
import compile.antlr.script.types.Type;
import org.apache.commons.lang3.StringUtils;
import java.util.LinkedList;
import java.util.List;

public class Scope extends Symbol {

    // 该 Scope 中的成员，包括变量、方法、类等
    public List<Symbol> symbols = new LinkedList<>();

    public void addSymbol(Symbol symbol) {
        symbols.add(symbol);
        symbol.enclosingScope = this;
    }

    public Variable getVariable(String name) {
        return getVariable(this, name);
    }

    public static Variable getVariable(Scope scope, String name) {
        for (Symbol symbol : scope.symbols) {
            if (symbol instanceof Variable && StringUtils.equals(symbol.name, name)) {
                return (Variable) symbol;
            }
        }

        return null;
    }

    public Variable getFuncVariable(String name, List<Type> paramTypes) {
        return getFuncVariable(this, name, paramTypes);
    }

    public static Variable getFuncVariable(Scope scope, String name, List<Type> paramTypes) {
        for (Symbol symbol : scope.symbols) {
            if (symbol instanceof Variable
                    && ((Variable) symbol).type instanceof FunctionType
                    && StringUtils.equals(symbol.name, name)) {
                Variable variable = (Variable) symbol;
                FunctionType funcType = (FunctionType) variable.type;

                if (funcType.matchParamTypes(paramTypes)) {
                    return variable;
                }
            }
        }

        return null;
    }

    public Function getFunction(String name, List<Type> paramTypes) {
        return getFunction(this, name, paramTypes);
    }

    public static Function getFunction(Scope scope, String name, List<Type> paramTypes) {
        for (Symbol symbol : scope.symbols) {
            if (symbol instanceof Function && StringUtils.equals(name, symbol.name)) {
                Function function = (Function) symbol;

                if (function.matchParamTypes(paramTypes)) {
                    return function;
                }
            }
        }

        return null;
    }

    public Klass getKlass(String name) {
        return getKlass(this, name);
    }

    public static Klass getKlass(Scope scope, String name) {
        for (Symbol symbol : scope.symbols) {
            if (symbol instanceof Klass && StringUtils.equals(name, symbol.name)) {
                return (Klass) symbol;
            }
        }

        return null;
    }

    public boolean containsSymbol(Symbol symbol) {
        return symbols.contains(symbol);
    }

    public boolean accessible(Symbol symbol) {
        return false;
    }

    @Override
    public String toString() {
        return "Scope: " + name;
    }
}
