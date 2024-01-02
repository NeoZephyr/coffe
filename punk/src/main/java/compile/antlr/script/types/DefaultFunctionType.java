package compile.antlr.script.types;

import compile.antlr.script.symbol.Scope;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DefaultFunctionType implements FunctionType {

    String name = null;
    Scope enclosingScope = null;
    public Type returnType = null;
    public List<Type> paramTypes = new LinkedList<Type>();

    // 对于未命名的类型，自动赋予名字
    private static int nameIndex = 1;

    public DefaultFunctionType (){
        name = "FunctionType" + nameIndex++;
    }

    @Override
    public Type getReturnType() {
        return returnType;
    }

    @Override
    public List<Type> getParamTypes() {
        return Collections.unmodifiableList(paramTypes);
    }

    @Override
    public boolean matchParamTypes(List<Type> paramTypes) {
        if (paramTypes.size() != this.paramTypes.size()) {
            return false;
        }

        boolean match = true;

        for (int i = 0; i < paramTypes.size(); i++) {
            Type t1 = this.paramTypes.get(i);
            Type t2 = paramTypes.get(i);

            if (!t1.isType(t2)) {
                match = false;
                break;
            }
        }

        return match;
    }

    @Override
    public boolean isType(Type type) {
        if (type instanceof FunctionType) {
            return isType(this, (FunctionType) type);
        }
        return false;
    }

    public static boolean isType(FunctionType type1, FunctionType type2) {
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
    public Scope getEnclosingScope() {
        return enclosingScope;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "FunctionType";
    }
}