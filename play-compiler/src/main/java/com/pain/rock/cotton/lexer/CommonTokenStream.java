package com.pain.rock.cotton.lexer;

import java.util.ArrayList;
import java.util.List;

public class CommonTokenStream implements TokenStream {

    private Lexer lexer;
    private int pos = -1;
    private List<Token> tokens = new ArrayList<>(100);

    public CommonTokenStream(Lexer lexer) {
        this.lexer = lexer;
    }

    @Override
    public Token read() {
        int p = pos + 1;

        if (p < tokens.size()) {
            return tokens.get(p);
        }

        load(1);

        if (p < tokens.size()) {
            pos = p;
            return tokens.get(p);
        } else {
            return null;
        }
    }

    /**
     * - - - - - - - - - -
     * 0 1 2 3 4 5 6 7 8 9
     *       S   E
     */
    @Override
    public Token peek(int offset) {
        int p = pos + offset;

        if (p < 0) {
            return null;
        }

        if (p < tokens.size()) {
            return tokens.get(p);
        }

        int n = p - tokens.size() + 1;
        load(n);

        if (p < tokens.size()) {
            return tokens.get(p);
        } else {
            return null;
        }
    }

    @Override
    public boolean seek(int offset) {
        int p = pos + offset;

        if (p < 0) {
            return false;
        }

        if (p < tokens.size()) {
            pos = p;
            return true;
        }

        return false;
    }

    private void load(int n) {
        for (int i = 0; i < n; ++i) {
            Token token = lexer.nextToken();

            if (token != null) {
                tokens.add(token);
            } else {
                return;
            }
        }
    }
}
