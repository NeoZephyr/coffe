package com.pain.rock.antlr.script;

import com.pain.rock.antlr.script.symbol.Variable;

import java.util.HashMap;
import java.util.Map;

public class PlayObject {
    public Map<Variable, Object> fields = new HashMap<>();

    public Object getValue(Variable variable) {
        Object o = fields.get(variable);

        if (o == null) {
            return NullObject.instance();
        }

        return o;
    }

    public void setValue(Variable variable, Object value) {
        fields.put(variable, value);
    }
}