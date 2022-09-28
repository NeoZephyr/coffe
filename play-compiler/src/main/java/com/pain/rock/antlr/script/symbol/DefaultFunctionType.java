package com.pain.rock.antlr.script.symbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultFunctionType implements FunctionType {

    protected String name;
    protected Scope enclosingScope;
    protected Type returnType;
    protected List<Type> paramTypes = new ArrayList<>();

    // 对于未命名的类型，自动赋予名字
    private static int nameIndex = 1;

    public DefaultFunctionType() {
        name = "FunctionType" + nameIndex++;
    }

    @Override
    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    @Override
    public List<Type> getParamTypes() {
        return Collections.unmodifiableList(paramTypes);
    }

    public void addParamType(Type type) {
        paramTypes.add(type);
    }

    @Override
    public boolean matchParamTypes(List<Type> paramTypes) {
        if (this.paramTypes.size() != paramTypes.size()) {
            return false;
        }

        for (int i = 0; i < paramTypes.size(); i++) {
            Type type1 = this.paramTypes.get(i);
            Type type2 = paramTypes.get(i);

            if (!type1.isType(type2)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Scope getEnclosingScope() {
        return enclosingScope;
    }

    @Override
    public boolean isType(Type type) {
        if (type instanceof FunctionType) {
            return isType(this, (FunctionType) type);
        }
        return false;
    }

    @Override
    public String toString() {
        return "FunctionType";
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
}