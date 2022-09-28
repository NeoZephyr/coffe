package com.pain.rock.antlr.script;

import com.pain.rock.antlr.script.symbol.Variable;

public interface LeftValue {

    Object getValue();

    void setValue(Object value);

    Variable getVariable();

    PlayObject getValueContainer();
}