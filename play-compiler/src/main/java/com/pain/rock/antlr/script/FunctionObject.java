package com.pain.rock.antlr.script;

import com.pain.rock.antlr.script.symbol.Function;
import com.pain.rock.antlr.script.symbol.Variable;

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