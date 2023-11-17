package compile.antlr.script.scope;

import compile.antlr.script.symbol.*;
import org.apache.commons.lang3.StringUtils;
import java.util.LinkedList;
import java.util.List;

public class Scope {

    // 该 Scope 中的成员，包括变量、方法、类等
    private List<Symbol> symbols = new LinkedList<>();

    public String name;

    public void addSymbol(Symbol symbol) {
        symbols.add(symbol);
        symbol.enclosingScope = this;
    }

    public VarSymbol getVarSymbol(String name) {
        return getVarSymbol(this, name);
    }

    public static VarSymbol getVarSymbol(Scope scope, String name) {
        for (Symbol symbol : scope.symbols) {
            if (symbol instanceof VarSymbol && StringUtils.equals(symbol.name, name)) {
                return (VarSymbol) symbol;
            }
        }

        return null;
    }

    public VarSymbol getFuncVarSymbol(String name, List<Type> paramTypes) {
        return getFuncVarSymbol(this, name, paramTypes);
    }

    public static VarSymbol getFuncVarSymbol(Scope scope, String name, List<Type> paramTypes) {
        for (Symbol symbol : scope.symbols) {
            if (symbol instanceof VarSymbol
                    && ((VarSymbol) symbol).getType() instanceof FuncType
                    && StringUtils.equals(symbol.name, name)) {
                VarSymbol varSymbol = (VarSymbol) symbol;
                FuncType funcType = (FuncType) varSymbol.getType();

                if (funcType.matchParamTypes(paramTypes)) {
                    return varSymbol;
                }
            }
        }

        return null;
    }

    public FuncSymbol getFuncSymbol(String name, List<Type> paramTypes) {
        return getFuncSymbol(this, name, paramTypes);
    }

    public static FuncSymbol getFuncSymbol(Scope scope, String name, List<Type> paramTypes) {
        for (Symbol symbol : scope.symbols) {
            if (symbol instanceof FuncSymbol && StringUtils.equals(name, symbol.name)) {
                FuncSymbol funcSymbol = (FuncSymbol) symbol;

                if (funcSymbol.matchParamTypes(paramTypes)) {
                    return funcSymbol;
                }
            }
        }

        return null;
    }

    public KlassSymbol getKlassSymbol(String name) {
        return getKlassSymbol(this, name);
    }

    public static KlassSymbol getKlassSymbol(Scope scope, String name) {
        for (Symbol symbol : scope.symbols) {
            if (symbol instanceof KlassSymbol && StringUtils.equals(name, symbol.name)) {
                return (KlassSymbol) symbol;
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
