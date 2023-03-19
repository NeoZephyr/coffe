package compile.antlr.script.symbol;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Function extends Scope implements FunctionType {

    protected List<Variable> params = new ArrayList<>();

    protected Type returnType;

    // 闭包变量，即它所引用的外部环境变量
    public Set<Variable> closureVariables;

    private List<Type> paramTypes;

    public Function(String name, Scope scope, ParserRuleContext ctx) {
        this.name = name;
        this.enclosingScope = scope;
        this.ctx = ctx;
    }

    @Override
    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public void addParam(Variable variable) {
        params.add(variable);
    }

    @Override
    public List<Type> getParamTypes() {
        if (paramTypes == null) {
            paramTypes = new ArrayList<>();
        }

        for (Variable param : params) {
            paramTypes.add(param.type);
        }

        return paramTypes;
    }

    @Override
    public boolean matchParamTypes(List<Type> paramTypes) {
        if (params.size() != paramTypes.size()) {
            return false;
        }

        for (int i = 0; i < paramTypes.size(); i++) {
            Variable v = params.get(i);
            Type type = paramTypes.get(i);

            if (!v.type.isType(type)) {
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

    /**
     * 是否为类方法
     */
    public boolean isMethod() {
        return enclosingScope instanceof Klass;
    }

    public boolean isConstructor() {
        if (enclosingScope instanceof Klass) {
            return enclosingScope.name.equals(name);
        }

        return false;
    }

    @Override
    public String toString() {
        return "Function " + name;
    }
}
