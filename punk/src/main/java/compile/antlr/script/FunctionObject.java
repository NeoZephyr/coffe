package compile.antlr.script;

import compile.antlr.script.symbol.Function;
import compile.antlr.script.symbol.Variable;

public class FunctionObject extends PlayObject {

    public Function function = null;
    public Variable variable = null;

    public FunctionObject(Function function) {
        this.function = function;
    }

    protected void setFunction(Function function) {
        this.function = function;
    }
}