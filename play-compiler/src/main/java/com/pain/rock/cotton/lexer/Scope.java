package com.pain.rock.cotton.lexer;

import java.util.HashMap;

public class Scope {
    private HashMap<String, Symbol> table = new HashMap<>();
    private Scope enclosingScope;

    public Scope(Scope enclosingScope) {
        this.enclosingScope = enclosingScope;
    }

    public void put(String s, Symbol symbol) {
        table.put(s, symbol);
    }

    public Symbol get(String s) {
        for (Scope scope = this; scope != null; scope = scope.enclosingScope) {
            Symbol symbol = scope.table.get(s);

            if (symbol != null) {
                return symbol;
            }
        }

        return null;
    }
}
