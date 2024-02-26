package compile.craft.frontend;

import compile.craft.frontend.tokens.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static compile.craft.frontend.CharUtils.*;
import static compile.craft.frontend.ErrorCode.INVALID_CHARACTER;

@Slf4j
public class Lexer {

    public Source source;

    public Lexer(Source source) {
        this.source = source;
    }

    /**
     * verify number end
     *
     */
    public Token nextToken() throws IOException {
        skip();

        char c = source.current();

        if (c == EOF) {
            return new EofToken(source);
        }

        if (CharUtils.isIdentifierChar(c)) {
            return new WordToken(source);
        }

        if (CharUtils.isDigit(c)) {
            return new NumberToken(source);
        }

        if (CharUtils.isSingleQuotes(c)) {
            return new CharToken(source);
        }

        if (CharUtils.isQuotes(c)) {
            return new StringToken(source);
        }

        /**
         *
         * (a+b)^100.12==+100-20
         *
         * 考虑 +/-/. 之后跟数值，是不是作为 number
         *
         * if c is digit
         *     process number
         * if c in ['+', '-', '.'] and next is digit
         *     if lastToken is null or lastToken is not value or lastToken is operator
         *         retreat
         *         process number
         */
        if (TokenKind.SPECIAL_SYMBOL_HEADERS.containsKey(c)) {
            return new SpecialSymbolToken(source);
        }

        Token token = new ErrorToken(source, INVALID_CHARACTER, String.valueOf(c));
        source.advance(); // consume character
        return token;
    }

    private void skip() throws IOException {
        char c = source.current();

        while (true) {
            if (isBlank(c)) {
                c = source.advance();
                continue;
            }

            if (c == '/' && source.peek() == '/') {
                while ((c != '\n') && (c != EOF)) {
                    c = source.advance();
                }
                continue;
            }

            if (c == '/' && source.peek() == '*') {
                source.advance();
                c = source.advance();

                while (c != EOF) {
                    if (c == '*' && source.peek() == '/') {
                        break;
                    }
                    c = source.advance();
                }
                // TODO 错误了
                continue;
            }

            break;
        }
    }
}
