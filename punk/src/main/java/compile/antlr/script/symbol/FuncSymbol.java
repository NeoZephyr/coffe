package compile.antlr.script.symbol;

import compile.antlr.script.scope.KlassScope;
import compile.antlr.script.scope.Scope;
import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class FuncSymbol extends Symbol implements FuncType {

    // 参数
    private List<VarSymbol> params = new LinkedList<>();

    // 闭包变量，即引用的外部环境变量
    private Set<VarSymbol> closureVars = null;

    private List<Type> paramTypes = null;

    // 返回值
    private Type returnType = null;

    public FuncSymbol(String name, Scope enclosingScope, ParserRuleContext ctx) {
        this.name = name;
        this.enclosingScope = enclosingScope;
        this.ctx = ctx;
    }

    @Override
    public Type getReturnType() {
        return returnType;
    }

    @Override
    public List<Type> getParamTypes() {
        if (paramTypes != null) {
            return paramTypes;
        }

        paramTypes = new LinkedList<>();

        for (VarSymbol param : params) {
            paramTypes.add(param.type);
        }

        return paramTypes;
    }

    public boolean matchParamTypes(List<Type> paramTypes) {
        if (params.size() != paramTypes.size()) {
            return false;
        }

        for (int i = 0; i < paramTypes.size(); ++i) {
            VarSymbol symbol = params.get(i);
            Type type = paramTypes.get(i);

            if (!symbol.type.isType(type)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isType(Type type) {
        if (type instanceof FuncType) {
            return isType(this, (FuncType) type);
        }

        return false;
    }

    public boolean isKlassMethod() {
        return enclosingScope instanceof KlassScope;
    }

    /**
     * 该函数是不是类的构建函数
     */
    public boolean isConstructor() {
        if (enclosingScope instanceof KlassScope) {
            return StringUtils.equals(name, enclosingScope.name);
        }

        return true;
    }

    public static boolean isType(FuncType type1, FuncType type2) {
        if (type1 == type2) {
            return true;
        }

        if (!type1.getReturnType().isType(type2.getReturnType())) {
            return false;
        }

        List<Type> paramTypes1 = type1.getParamTypes();
        List<Type> paramTypes2 = type2.getParamTypes();

        if (paramTypes1.size() != paramTypes2.size()) {
            return false;
        }

        for (int i = 0; i < paramTypes1.size(); i++) {
            if (!paramTypes1.get(i).isType(paramTypes2.get(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "Function " + name;
    }
}