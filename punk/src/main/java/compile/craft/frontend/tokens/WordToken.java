package compile.craft.frontend.tokens;

import compile.craft.frontend.Source;
import compile.craft.frontend.Token;
import compile.craft.frontend.TokenKind;
import lombok.SneakyThrows;

import java.io.IOException;

import static compile.craft.frontend.CharUtils.isIdentifierChar;
import static compile.craft.frontend.TokenKind.IDENTIFIER;
import static compile.craft.frontend.TokenKind.RESERVED_WORDS;

public class WordToken extends Token {

    public WordToken(Source source) {
        super(source);
    }

    @SneakyThrows
    @Override
    protected void extract() {
        StringBuilder sb = new StringBuilder();
        while (isIdentifierChar(source.current())) {
            sb.append(source.current());
            source.advance();
        }

        String text = sb.toString();
        kind = RESERVED_WORDS.getOrDefault(text, IDENTIFIER);
    }
}