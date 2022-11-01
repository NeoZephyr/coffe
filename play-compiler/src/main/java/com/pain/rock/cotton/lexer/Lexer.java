package com.pain.rock.cotton.lexer;

public class Lexer {

    boolean fetchedEOF = false;

    public Token nextToken() {
        if (fetchedEOF) {
            return null;
        }

        return null;
    }
}
