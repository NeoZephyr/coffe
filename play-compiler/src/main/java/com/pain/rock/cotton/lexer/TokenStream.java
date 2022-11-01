package com.pain.rock.cotton.lexer;

public interface TokenStream {

    Token read();

    Token peek(int offset);

    boolean seek(int offset);
}
