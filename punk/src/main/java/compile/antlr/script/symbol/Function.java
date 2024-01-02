package compile.antlr.script.symbol;

import compile.antlr.script.types.DefaultFunctionType;
import compile.antlr.script.types.FunctionType;
import compile.antlr.script.types.Type;
import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Function extends Scope implements FunctionType {

    // 参数
    public List<Variable> params = new LinkedList<>();

    // 闭包变量，即引用的外部环境变量
    public Set<Variable> closureVars = null;

    private List<Type> paramTypes = null;

    // 返回值
    public Type returnType = null;

    public Function(String name, Scope enclosingScope, ParserRuleContext ctx) {
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

        for (Variable param : params) {
            paramTypes.add(param.type);
        }

        return paramTypes;
    }

    public boolean matchParamTypes(List<Type> paramTypes) {
        if (params.size() != paramTypes.size()) {
            return false;
        }

        for (int i = 0; i < paramTypes.size(); ++i) {
            Variable symbol = params.get(i);
            Type type = paramTypes.get(i);

            if (!symbol.type.isType(type)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isType(Type type) {
        if (type instanceof FunctionType) {
            return DefaultFunctionType.isType(this, (FunctionType) type);
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

    public boolean isMethod() {
        return enclosingScope instanceof Klass;
    }

    /**
     * 该函数是不是类的构建函数
     */
    public boolean isConstructor() {
        if (enclosingScope instanceof Klass) {
            return StringUtils.equals(name, enclosingScope.name);
        }

        return true;
    }

    @Override
    public String toString() {
        return "Function " + name;
    }
}