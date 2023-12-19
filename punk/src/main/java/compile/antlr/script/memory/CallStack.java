package compile.antlr.script.memory;

import compile.antlr.script.symbol.Block;
import compile.antlr.script.symbol.Scope;
import compile.antlr.script.symbol.Variable;

public class CallStack {

    Scope scope = null;

    CallStack parent = null;

    ActivationRecord record = null;

    public CallStack(Block block) {
        this.scope = block;
        this.record = new ActivationRecord();
    }

    public CallStack(KlassObject object) {
        this.scope = object.klass;
        this.record = object;
    }

    public CallStack(FunctionObject object) {
        this.scope = object.function;
        this.record = object;
    }

    protected boolean contains(Variable symbol) {
        if ((record != null) && !record.empty()) {
            return record.contains(symbol);
        }

        return false;
    }

    @Override
    public String toString() {
        String s = scope.toString();

        if (parent != null) {
            s += " -> " + parent;
        }

        return s;
    }
}