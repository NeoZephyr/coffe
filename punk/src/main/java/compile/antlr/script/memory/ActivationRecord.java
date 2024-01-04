package compile.antlr.script.memory;

import compile.antlr.script.symbol.Variable;

import java.util.HashMap;
import java.util.Map;

public class ActivationRecord {

    public Map<Variable, Object> members = new HashMap<>();

    public boolean empty() {
        return members.isEmpty();
    }

    public boolean contains(Variable symbol) {
        return members.containsKey(symbol);
    }

    public Object getValue(Variable symbol) {
        Object obj = members.get(symbol);

        if (obj == null) {
            obj = KlassObject.NULL_OBJECT;
        }

        return obj;
    }

    public void setValue(Variable symbol, Object value) {
        members.put(symbol, value);
    }
}
