package com.pain.rock.antlr.script;

public class ReturnObject {
    public Object returnValue = null;

    public ReturnObject(Object value) {
        this.returnValue = value;
    }

    @Override
    public String toString() {
        return "ReturnObject";
    }
}