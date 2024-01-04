package compile.antlr.script.memory;

import compile.antlr.script.symbol.Block;
import compile.antlr.script.symbol.Scope;
import compile.antlr.script.symbol.Variable;

public class StackFrame {

    public Scope scope = null;

    public StackFrame parent = null;

    public ActivationRecord record = null;

    public StackFrame(Block block) {
        this.scope = block;
        this.record = new ActivationRecord();
    }

    public StackFrame(KlassObject object) {
        this.scope = object.klass;
        this.record = object;
    }

    public StackFrame(FunctionObject object) {
        this.scope = object.function;
        this.record = object;
    }

    public boolean contains(Variable symbol) {
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