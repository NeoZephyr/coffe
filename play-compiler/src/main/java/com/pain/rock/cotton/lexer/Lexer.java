package com.pain.rock.cotton.lexer;

import java.io.IOException;
import java.util.HashMap;

public class Lexer {
    public int line = 1;
    private char peek = ' ';
    private HashMap<String, Word> words = new HashMap<>();

    public void reserve(Word word) {
        words.put(word.lexeme, word);
    }

    public Lexer() {
        reserve(new Word(Tag.TRUE, "true"));
        reserve(new Word(Tag.FALSE, "false"));
    }

    // //, /* */ 处理
    // < <= == != >= > 处理
    // 2. 3.14 .5 处理
    public Token scan() throws IOException {
        for (;; peek = (char) System.in.read()) {
            if (peek == ' ' || peek == '\t') {
                continue;
            } else if (peek == '\n') {
                line++;
            } else {
                break;
            }
        }

        if (Character.isDigit(peek)) {
            int v = 0;

            do {
                v = 10 * v + Character.digit(peek, 10);
                peek = (char) System.in.read();
            } while (Character.isDigit(peek));

            return new Num(v);
        }

        if (Character.isLetter(peek)) {
            StringBuilder sb = new StringBuilder();

            do {
                sb.append(peek);
                peek = (char) System.in.read();
            } while (Character.isLetterOrDigit(peek));

            String lexeme = sb.toString();
            Word w = words.get(lexeme);

            if (w != null) {
                return w;
            }

            w = new Word(Tag.ID, lexeme);
            words.put(lexeme, w);
            return w;
        }

        Token token = new Token(peek);
        peek = ' ';
        return token;
    }
}
