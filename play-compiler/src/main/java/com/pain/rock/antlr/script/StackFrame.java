package com.pain.rock.antlr.script;

import com.pain.rock.antlr.script.symbol.Block;
import com.pain.rock.antlr.script.symbol.Scope;
import com.pain.rock.antlr.script.symbol.Variable;

public class StackFrame {
    public Scope scope = null;
    public StackFrame parentFrame = null;
    public PlayObject object = null;

    public StackFrame(Block scope) {
        this.scope = scope;
        this.object = new PlayObject();
    }

    public StackFrame(KlassObject object) {
        this.scope = object.type;
        this.object = object;
    }

    public StackFrame(FunctionObject object) {
        this.scope = object.function;
        this.object = object;
    }

    public boolean contains(Variable variable) {
        if (object != null && object.fields != null) {
            return object.fields.containsKey(variable);
        }

        return false;
    }

    @Override
    public String toString() {
        String text = "" + scope;

        if (parentFrame != null) {
            text += " -> " + parentFrame;
        }

        return text;
    }
}