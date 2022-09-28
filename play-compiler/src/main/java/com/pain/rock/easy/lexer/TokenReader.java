package com.pain.rock.easy.lexer;

public interface TokenReader {

    /**
     * 取出。为空，返回 null
     */
    Token read();

    /**
     * 不取出。为空，返回 null
     */
    Token peek();

    /**
     * 回退
     */
    void unread();

    int getPosition();

    void setPosition(int position);
}
