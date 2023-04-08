package compile.craft;

import lombok.extern.slf4j.Slf4j;

import static compile.craft.CharUtils.*;

@Slf4j
public class Lexer {

    boolean fetchedEOF = false;

    private final String source;
    private int pos = 0;
    private char ch;
    private int line = 0;
    private int col = 0;


//    STRING_LITERAL:     '"' (~["\\\r\n] | EscapeSequence)* '"';
//
//    NULL_LITERAL:       'null';
//
//// Separators
//    XOR_ASSIGN:         '^=';
//    MOD_ASSIGN:         '%=';
//    COLONCOLON:         '::';
//
//// Additional symbols not defined in the lexical specification
//    AT:                 '@';
//
//// Whitespace and comments
//
//    WS:                 [ \t\r\n\u000C]+ -> channel(HIDDEN);
//    COMMENT:            '/*' .*? '*/'    -> channel(HIDDEN);
//    LINE_COMMENT:       '//' ~[\r\n]*    -> channel(HIDDEN);

    public Lexer(String source) {
        this.source = source;
    }

    /**
     * 1. line 维护
     * 2. col 维护
     * 3. verify number end
     *
     */
    public Token nextToken() {
        if (fetchedEOF) {
            return null;
        }

        while (true) {
            do {
                advance();
            } while (isBlank(ch));

            if (ch == EOF) {
                fetchedEOF = true;
                return null;
            }

            // 处理 // 注释

            if (isIdentifierStart(ch)) {
                return scanIdentifier();
            }

            if (ch == '.') {
                advance();

                if (isDigit(ch)) {
                    return scanNumber();
                } else if (ch == '.') {
                    if (peek() == '.') {
                        advance();
                        return new Token(TokenKind.ELLIPSIS);
                    }
                }

                retreat();
                return new Token(TokenKind.DOT);
            }

            if (CharUtils.isDigit(ch)) {
                return scanNumber();
            }

            // '\\' 'u005c'? [btnfr"'\\]
            char b = '\u005cb';
            char c = ' ';
            System.out.println("ab\ndd");
            c = '\n';

            // CHAR_LITERAL:       '\'' (~['\\\r\n] | EscapeSequence) '\'';
            // EscapeSequence: '\\' 'u005c'? [btnfr"'\\]
            // EscapeSequence: '\\' 'u005c'? ([0-3]? [0-7])? [0-7]
            // EscapeSequence: '\\' 'u'+ HexDigit HexDigit HexDigit HexDigit
            if (ch == '\'') {
                System.out.println('a');
            }
        }
    }

    private Token scanNumber() {
        int start = pos - 1;
        TokenKind tokenKind = null;

        if (ch == '0') {
            advance();

            // HEX_LITERAL:        '0' [xX] [0-9a-fA-F] ([0-9a-fA-F_]* [0-9a-fA-F])? [lL]?;
            // HEX_FLOAT_LITERAL:  '0' [xX] (HexDigits '.'? | HexDigits? '.' HexDigits) [pP] [+-]? Digits [fFdD]?;
            // HexDigits: HexDigit ((HexDigit | '_')* HexDigit)?;
            // Digits: [0-9] ([0-9_]* [0-9])?;

            if (ch == 'x' || ch == 'X') {
                if (peek() == '.') {
                    advance();
                    scanHexFraction(false);
                    tokenKind = TokenKind.HEX_FLOAT_LITERAL;
                } else {
                    scanHex();

                    if (ch == '.') {
                        scanHexFraction(true);
                        tokenKind = TokenKind.HEX_FLOAT_LITERAL;
                    } else if (ch == 'p' || ch == 'P') {
                        scanExp();
                        tokenKind = TokenKind.HEX_FLOAT_LITERAL;
                    } else if (ch == 'f' || ch == 'F' || ch == 'd' || ch == 'D') {
                        tokenKind = TokenKind.HEX_FLOAT_LITERAL;
                    } else if (ch == 'l' || ch == 'L') {
                        tokenKind = TokenKind.HEX_LITERAL;
                    } else {
                        retreat();
                        tokenKind = TokenKind.HEX_LITERAL;
                    }
                }
            } else if (ch == 'b' || ch == 'B') {
                // BINARY_LITERAL:     '0' [bB] [01] ([01_]* [01])? [lL]?;
                scanBit();

                if ((ch != 'l') && (ch != 'L')) {
                    retreat();
                }

                tokenKind = TokenKind.BINARY_LITERAL;
            } else if (ch == '_' || isOct(ch)) {
                // OCT_LITERAL:        '0' '_'* [0-7] ([0-7_]* [0-7])? [lL]?;
                // FLOAT_LITERAL:      (Digits '.' Digits? | '.' Digits) ExponentPart? [fFdD]?;
                // FLOAT_LITERAL:      Digits (ExponentPart [fFdD]? | [fFdD]);
                scanOct();

                if (isDigit(ch)) {
                    scanDigit();

                    if (ch == '.') {
                        scanFraction();
                    } else if (ch == 'e' || ch == 'E') {
                        scanExp();
                    } else if ((ch != 'f') && (ch != 'F') && (ch != 'd') && (ch != 'D')) {
                        error("invalid oct literal");
                    }

                    tokenKind = TokenKind.FLOAT_LITERAL;
                } else if (ch == '.') {
                    scanFraction();
                    tokenKind = TokenKind.FLOAT_LITERAL;
                } else if (ch == 'e' || ch == 'E') {
                    scanExp();
                    tokenKind = TokenKind.FLOAT_LITERAL;
                } else if (ch == 'd' || ch == 'D' || ch == 'f' || ch == 'F') {
                    tokenKind = TokenKind.FLOAT_LITERAL;
                } else if (ch == 'l' || ch == 'L') {
                    tokenKind = TokenKind.OCT_LITERAL;
                } else {
                    retreat();
                    tokenKind = TokenKind.OCT_LITERAL;
                }
            } else {
                // FLOAT_LITERAL:      (Digits '.' Digits? | '.' Digits) ExponentPart? [fFdD]?;
                // FLOAT_LITERAL:      Digits (ExponentPart [fFdD]? | [fFdD]);
                // DECIMAL_LITERAL:    ('0' | [1-9] (Digits? | '_'+ Digits)) [lL]?;
                // ExponentPart:       [eE] [+-]? Digits;

                if (isDigit(ch)) {
                    do {
                        advance();
                    } while (isDigit(ch));

                    if (ch == '.') {
                        scanFraction();
                        tokenKind = TokenKind.FLOAT_LITERAL;
                    } else if (ch == 'e' || ch == 'E') {
                        scanExp();
                        tokenKind = TokenKind.FLOAT_LITERAL;
                    } else if ((ch == 'f') || ch == 'F' || ch == 'd' || ch == 'D') {
                        tokenKind = TokenKind.FLOAT_LITERAL;
                    } else {
                        error("invalid float literal");
                    }
                } else if (ch == '.') {
                    scanFraction();
                    tokenKind = TokenKind.FLOAT_LITERAL;
                } else if (ch == 'e' || ch == 'E') {
                    scanExp();
                    tokenKind = TokenKind.FLOAT_LITERAL;
                } else if (ch == 'l' || ch == 'L') {
                    tokenKind = TokenKind.DECIMAL_LITERAL;
                } else if (ch == 'f' || ch == 'F' || ch == 'd' || ch == 'D') {
                    tokenKind = TokenKind.FLOAT_LITERAL;
                } else {
                    retreat();
                    tokenKind = TokenKind.DECIMAL_LITERAL;
                }
            }
        } else {
            // FLOAT_LITERAL:      (Digits '.' Digits? | '.' Digits) ExponentPart? [fFdD]?;
            // FLOAT_LITERAL:      Digits (ExponentPart [fFdD]? | [fFdD]);
            // DECIMAL_LITERAL:    ('0' | [1-9] (Digits? | '_'+ Digits)) [lL]?;
            // ExponentPart:       [eE] [+-]? Digits;
            if (ch == '.') {
                scanDigit();

                if (ch == 'e' || ch == 'E') {
                    scanExp();
                }

                tokenKind = TokenKind.FLOAT_LITERAL;
            } else {
                scanDigit(true);

                if (ch == '.') {
                    scanFraction();
                    tokenKind = TokenKind.FLOAT_LITERAL;
                } else if (ch == 'e' || ch == 'E') {
                    scanExp();
                    tokenKind = TokenKind.FLOAT_LITERAL;
                } else if (ch == 'f' || ch == 'F' || ch == 'd' || ch == 'D') {
                    tokenKind = TokenKind.FLOAT_LITERAL;
                } else if (ch == 'l' || ch == 'L') {
                    tokenKind = TokenKind.DECIMAL_LITERAL;
                } else {
                    retreat();
                    tokenKind = TokenKind.DECIMAL_LITERAL;
                }
            }
        }

        // verifyEndOfNumber
        String lexeme = source.substring(start, pos);
        return new Token(tokenKind, lexeme);
    }

    private Token scanIdentifier() {
        int start = pos - 1;

        do {
            advance();
        } while (isIdentifierChar(ch));

        String lexeme = source.substring(start, pos);
        retreat();

        if (Token.isKeyword(lexeme)) {
            TokenKind kind = Token.kind(lexeme);
            return new Token(kind, kind.literal);
        } else {
            return new Token(TokenKind.IDENTIFIER, lexeme);
        }
    }

    private void scanHex() {
        advance();

        if (!isHex(ch)) {
            error("invalid hexadecimal literal");
        }

        do {
            if (ch == '_') {
                do {
                    advance();
                } while(ch == '_');

                if (!isHex(ch)) {
                    error("invalid hexadecimal literal");
                }
            }

            do {
                advance();
            } while (isHex(ch));
        } while (ch == '_');
    }

    private void scanBit() {
        advance();

        if (!isBit(ch)) {
            error("invalid binary literal");
        }

        do {
            if (ch == '_') {
                do {
                    advance();
                } while(ch == '_');

                if (!isBit(ch)) {
                    error("invalid binary literal");
                }
            }

            do {
                advance();
            } while (isBit(ch));
        } while (ch == '_');
    }

    private void scanOct() {
        do {
            if (ch == '_') {
                do {
                    advance();
                } while(ch == '_');

                if (!isDigit(ch)) {
                    error("invalid octal literal");
                }

                if (!isOct(ch)) {
                    return;
                }
            }

            do {
                advance();
            } while (isOct(ch));
        } while (ch == '_');
    }

    private void scanDigit(boolean hasDigit) {
        advance();

        if (hasDigit) {
            if ((ch != '_') && !isDigit(ch)) {
                return;
            }
        } else if (!isDigit(ch)) {
            error("invalid decimal literal");
        }

        do {
            if (ch == '_') {
                do {
                    advance();
                } while(ch == '_');

                if (!isDigit(ch)) {
                    error("invalid decimal literal");
                }
            }

            do {
                advance();
            } while (isDigit(ch));
        } while (ch == '_');
    }

    private void scanDigit() {
        scanDigit(false);
    }

    // FLOAT_LITERAL:      (Digits '.' Digits? | '.' Digits) ExponentPart? [fFdD]?;
    private void scanFraction() {
        if (isDigit(peek())) {
            scanDigit();
        } else {
            advance();
        }

        if (ch == 'e' || ch == 'E') {
            char c = peek();

            if (c == '+' || c == '-') {
                advance();
            }

            scanDigit();
        }

        if ((ch != 'f') && (ch != 'F') && (ch != 'd') && (ch != 'D')) {
            retreat();
        }
    }

    // HEX_FLOAT_LITERAL:  '0' [xX] (HexDigits '.'? | HexDigits? '.' HexDigits) [pP] [+-]? Digits [fFdD]?;
    private void scanHexFraction(boolean hasDigit) {
        if (hasDigit) {
            if (isHex(peek())) {
                scanHex();
            } else {
                advance();
            }
        } else {
            scanHex();
        }

        if ((ch != 'p') && (ch != 'P')) {
            error("invalid hexadecimal literal");
        }

        scanExp();
    }

    private void scanExp() {
        char c = peek();

        if (c == '+' || c == '-') {
            advance();
        }

        scanDigit();

        if ((ch != 'f') && (ch != 'F') && (ch != 'd') && (ch != 'D')) {
            retreat();
        }
    }

    private void advance() {
        if (pos >= source.length()) {
            ch = CharUtils.EOF;
            return;
        }

        ch = source.charAt(pos++);
    }

    private void retreat() {
        if (ch != EOF) {
            if (--pos < 0) {
                error("tokenizer exceed beginning of source");
            }
        }
    }

    private char peek() {
        if (pos >= source.length()) {
            return EOF;
        }

        return source.charAt(pos);
    }

    private void error(String msg) {
        log.error("lexer error: {}", msg);
        throw new RuntimeException(msg);
    }
}
