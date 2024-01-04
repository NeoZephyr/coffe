package compile.antlr.script.memory;

import compile.antlr.script.symbol.Function;
import compile.antlr.script.symbol.Variable;

public class FunctionObject extends ActivationRecord {

    public Function function = null;

    /**
     * 接收者所在的 scope。缺省是 function 的 enclosingScope，也就是词法的 Scope
     * 当赋值给一个函数型变量的时候，要修改 receiver 等于这个变量的 enclosingScope
     */
    public Variable receiver = null;

    public FunctionObject(Function function) {
        this.function = function;
    }

    protected void setFunction(Function function) {
        this.function = function;
    }
}
