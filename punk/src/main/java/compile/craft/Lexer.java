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

//
//    CHAR_LITERAL:       '\'' (~['\\\r\n] | EscapeSequence) '\'';
//
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
//
//    AT:                 '@';
//    ELLIPSIS:           '...';
//
//// Whitespace and comments
//
//    WS:                 [ \t\r\n\u000C]+ -> channel(HIDDEN);
//    COMMENT:            '/*' .*? '*/'    -> channel(HIDDEN);
//    LINE_COMMENT:       '//' ~[\r\n]*    -> channel(HIDDEN);

//    fragment EscapeSequence
//    : '\\' [btnfr"'\\]
//            | '\\' ([0-3]? [0-7])? [0-7]
//            | '\\' 'u'+ HexDigit HexDigit HexDigit HexDigit
//            ;


    public Lexer(String source) {
        this.source = source;
    }

    /**
     * 1. line 维护
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
            }

            if (CharUtils.isDigit(ch)) {
                return scanNumber();
            }

            break;
        }

        return null;
    }

    private Token scanNumber() {
        int start = pos;

        if (ch == '0') {
            advance();

            // HEX_LITERAL:        '0' [xX] [0-9a-fA-F] ([0-9a-fA-F_]* [0-9a-fA-F])? [lL]?;
            // HEX_FLOAT_LITERAL:  '0' [xX] (HexDigits '.'? | HexDigits? '.' HexDigits) [pP] [+-]? Digits [fFdD]?;
            // HexDigits: HexDigit ((HexDigit | '_')* HexDigit)?;
            // Digits: [0-9] ([0-9_]* [0-9])?;

            if (ch == 'x' || ch == 'X') {
                if (peek() == '.') {
                    scanHexFraction(false);
                    String lexeme = source.substring(start, pos + 1);
                    return new Token(TokenKind.HEX_FLOAT_LITERAL, lexeme);
                }

                scanHex();

                if (ch == '.') {
                    scanHexFraction(true);
                    String lexeme = source.substring(start, pos + 1);
                    return new Token(TokenKind.HEX_FLOAT_LITERAL, lexeme);
                } else if (ch == 'p' || ch == 'P') {
                    char c = peek();

                    if (c == '+' || c == '-') {
                        advance();
                    }

                    scanDigit();

                    if ((ch != 'f') && (ch != 'F') && (ch != 'd') && (ch != 'D')) {
                        retreat();
                    }

                    String lexeme = source.substring(start, pos + 1);
                    return new Token(TokenKind.HEX_FLOAT_LITERAL, lexeme);
                } else if (ch == 'l' || ch == 'L') {
                    // verifyEndOfNumber
                    String lexeme = source.substring(start, pos + 1);
                    return new Token(TokenKind.HEX_LITERAL, lexeme);
                } else {
                    retreat();
                    String lexeme = source.substring(start, pos + 1);
                    return new Token(TokenKind.HEX_LITERAL, lexeme);
                }
            } else if (ch == 'b' || ch == 'B') {
                // BINARY_LITERAL:     '0' [bB] [01] ([01_]* [01])? [lL]?;
                scanBit();

                if (ch == 'l' || ch == 'L') {
                    String lexeme = source.substring(start, pos + 1);
                    return new Token(TokenKind.BINARY_LITERAL, lexeme);
                } else {
                    retreat();
                    String lexeme = source.substring(start, pos + 1);
                    return new Token(TokenKind.BINARY_LITERAL, lexeme);
                }
            } else if (ch == '_' || isOct(ch)) {
                // OCT_LITERAL:        '0' '_'* [0-7] ([0-7_]* [0-7])? [lL]?;
                scanOct();

                // FLOAT_LITERAL:      (Digits '.' Digits? | '.' Digits) ExponentPart? [fFdD]?;
                // FLOAT_LITERAL:      Digits (ExponentPart [fFdD]? | [fFdD]);

                System.out.println(0_00___9900f);
                System.out.println(0_00___0d);
                System.out.println(0_00___0f);
                System.out.println(0_00_77__0L);
                System.out.println(0_00_77.0__0);
                System.out.println(0005.);
                System.out.println(0009.);
                System.out.println(0004e1);
                System.out.println(0009e1);

                if (isDigit(ch)) {
                    scanDigit();

                    if (ch == '.') {
                        scanFraction();
                        String lexeme = source.substring(start, pos + 1);
                        return new Token(TokenKind.FLOAT_LITERAL, lexeme);
                    } else if (ch == 'e' || ch == 'E') {
                        scanExp();
                        String lexeme = source.substring(start, pos + 1);
                        return new Token(TokenKind.FLOAT_LITERAL, lexeme);
                    } else if (ch == 'f' || ch == 'F' || ch == 'd' || ch == 'D') {
                        String lexeme = source.substring(start, pos + 1);
                        return new Token(TokenKind.FLOAT_LITERAL, lexeme);
                    }

                    error("invalid float literal");
                } else if (ch == '.') {
                    scanFraction();
                    String lexeme = source.substring(start, pos + 1);
                    return new Token(TokenKind.FLOAT_LITERAL, lexeme);
                } else if (ch == 'e' || ch == 'E') {
                    scanExp();
                    String lexeme = source.substring(start, pos + 1);
                    return new Token(TokenKind.FLOAT_LITERAL, lexeme);
                } else {
                    if (ch == 'l' || ch == 'L') {
                        String lexeme = source.substring(start, pos + 1);
                        return new Token(TokenKind.OCT_LITERAL, lexeme);
                    } else if (ch == 'd' || ch == 'D' || ch == 'f' || ch == 'F') {
                        String lexeme = source.substring(start, pos + 1);
                        return new Token(TokenKind.FLOAT_LITERAL, lexeme);
                    } else {
                        retreat();
                        String lexeme = source.substring(start, pos + 1);
                        return new Token(TokenKind.OCT_LITERAL, lexeme);
                    }
                }
            } else {
                // FLOAT_LITERAL:      (Digits '.' Digits? | '.' Digits) ExponentPart? [fFdD]?;
                // FLOAT_LITERAL:      Digits (ExponentPart [fFdD]? | [fFdD]);
                // DECIMAL_LITERAL:    ('0' | [1-9] (Digits? | '_'+ Digits)) [lL]?;
                // ExponentPart:       [eE] [+-]? Digits;

                if (ch == 'l' || ch == 'L') {
                    String lexeme = source.substring(start, pos + 1);
                    return new Token(TokenKind.DECIMAL_LITERAL, lexeme);
                }

                while (isDigit(ch)) {
                    advance();
                }

                if (ch == '.') {
                    scanFraction();
                    String lexeme = source.substring(start, pos + 1);
                    return new Token(TokenKind.FLOAT_LITERAL, lexeme);
                } else if (ch == 'e' || ch == 'E') {
                    scanExp();
                    String lexeme = source.substring(start, pos + 1);
                    return new Token(TokenKind.FLOAT_LITERAL, lexeme);
                } else if (ch == 'f' || ch == 'F' || ch == 'd' || ch == 'D') {
                    String lexeme = source.substring(start, pos + 1);
                    return new Token(TokenKind.FLOAT_LITERAL, lexeme);
                }

                error("invalid float literal");
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

                String lexeme = source.substring(start, pos + 1);
                return new Token(TokenKind.FLOAT_LITERAL, lexeme);
            } else {
                // TODO ignore beginning
                scanDigit();

                if (ch == '.') {
                    scanFraction();
                    String lexeme = source.substring(start, pos + 1);
                    return new Token(TokenKind.FLOAT_LITERAL, lexeme);
                } else if (ch == 'e' || ch == 'E') {
                    scanExp();
                    String lexeme = source.substring(start, pos + 1);
                    return new Token(TokenKind.FLOAT_LITERAL, lexeme);
                } else if (ch == 'f' || ch == 'F' || ch == 'd' || ch == 'D') {
                    String lexeme = source.substring(start, pos + 1);
                    return new Token(TokenKind.FLOAT_LITERAL, lexeme);
                } else if (ch == 'l' || ch == 'L') {
                    String lexeme = source.substring(start, pos + 1);
                    return new Token(TokenKind.DECIMAL_LITERAL, lexeme);
                }

                retreat();
                String lexeme = source.substring(start, pos + 1);
                return new Token(TokenKind.DECIMAL_LITERAL, lexeme);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(0b11_01);
        System.out.println(0B10_01L);
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

    private void scanDigit() {
        advance();

        if (!isDigit(ch)) {
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
            }
            advance();
        } else {
            scanHex();
        }

        if ((ch != 'p') && (ch != 'P')) {
            error("invalid hexadecimal literal");
        }

        char c = peek();

        if (c == '+' || c == '-') {
            advance();
        }

        scanDigit();

        if ((ch != 'f') && (ch != 'F') && (ch != 'd') && (ch != 'D')) {
            retreat();
        }
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
