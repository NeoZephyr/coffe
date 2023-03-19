package compile.antlr.script;

import compile.antlr.script.symbol.Variable;

public interface LeftValue {

    Object getValue();

    void setValue(Object value);

    Variable getVariable();

    PlayObject getValueContainer();
}