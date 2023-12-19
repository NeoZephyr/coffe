package compile.antlr.script.types;

import java.util.List;

public interface FunctionType extends Type {

    Type getReturnType();

    List<Type> getParamTypes();

    boolean matchParamTypes(List<Type> paramTypes);
}
