package compile.antlr.script.runtime;

import compile.antlr.script.memory.ActivationRecord;
import compile.antlr.script.memory.FunctionObject;
import compile.antlr.script.symbol.Super;
import compile.antlr.script.symbol.This;
import compile.antlr.script.symbol.Variable;

public class ValueRef {
    public Variable variable;
    public ActivationRecord record;

    public ValueRef(ActivationRecord record, Variable variable) {
        this.record = record;
        this.variable = variable;
    }

    public Object getValue() {
        if (variable instanceof This || variable instanceof Super) {
            return record;
        }

        return record.getValue(variable);
    }

    public void setValue(Object value) {
        record.setValue(variable, value);

        if (value instanceof FunctionObject) {
            ((FunctionObject) value).receiver = variable;
        }
    }

    public String toString() {
        return "value of " + variable.name + " : " + getValue();
    }
}
