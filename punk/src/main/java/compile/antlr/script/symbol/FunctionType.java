package compile.antlr.script.symbol;

import java.util.List;

public interface FunctionType extends Type {
    Type getReturnType();

    List<Type> getParamTypes();

    boolean matchParamTypes(List<Type> paramTypes);
}

