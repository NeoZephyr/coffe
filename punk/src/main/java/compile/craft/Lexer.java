package compile.craft;

import static compile.craft.CharUtils.*;

public class Lexer {

    boolean fetchedEOF = false;

    private final String text;
    private int pos = 0;
    private char ch;
    private int line = 0;

//
//    CHAR_LITERAL:       '\'' (~['\\\r\n] | EscapeSequence) '\'';
//
//    STRING_LITERAL:     '"' (~["\\\r\n] | EscapeSequence)* '"';
//
//    NULL_LITERAL:       'null';
//
//// Identifiers
//
//    IDENTIFIER:         Letter LetterOrDigit*;
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
//
//// Fragment rules
//
//    fragment Digits
//    : [0-9] ([0-9_]* [0-9])?
//    ;
//
//    fragment HexDigit
//    : [0-9a-fA-F]
//    ;
//
//    fragment HexDigits
//    : HexDigit ((HexDigit | '_')* HexDigit)?
//    ;
//
//    fragment ExponentPart
//    : [eE] [+-]? Digits
//    ;
//
//    fragment EscapeSequence
//    : '\\' [btnfr"'\\]
//            | '\\' ([0-3]? [0-7])? [0-7]
//            | '\\' 'u'+ HexDigit HexDigit HexDigit HexDigit
//            ;
//
//    fragment LetterOrDigit
//    : Letter
//    | [0-9]
//    ;
//
//    fragment Letter
//    : [a-zA-Z$_] // these are the "java letters" below 0x7F
//            | ~[\u0000-\u007F\uD800-\uDBFF] // covers all characters above 0x7F which are not a surrogate
//            | [\uD800-\uDBFF] [\uDC00-\uDFFF] // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
//    ;

    public Lexer(String text) {
        this.text = text;
    }

    // 1. hex, number
    public Token nextToken() {
        if (fetchedEOF) {
            return null;
        }

        while (true) {
            if (isBlank(ch)) {
                if (ch == '\n') {
                    line++;
                }

                ch = charAt(++pos);
                continue;
            }

            if (isFirstIdChar(ch)) {
                return scanId();
            }

            switch (ch) {
                case '0':
                    if (charAt(pos + 1) == 'x') {
                        return scanHex();
                    } else {
                    }
                    break;
                case '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9':
                    break;
            }
//            switch (ch) {
//                case '0':
//                    if (charAt(pos + 1) == 'x') {
//                        scanChar();
//                        scanChar();
//                        scanHexaDecimal();
//                    } else {
//                        scanNumber();
//                    }
//                    return;
//            }

            // fd
            /* fd
            */

            break;
        }

        return null;
    }

    private void scanChar() {
        ch = charAt(++pos);
    }

    // BINARY_LITERAL:     '0' [bB] [01] ([01_]* [01])? [lL]?;
    private Token scanBin() {
        int mark = pos;
        int bufSize = 1;

        scanChar();

        while (true) {
            ch = charAt(++pos);

            if (!isBit(ch)) {
                break;
            }

            bufSize++;
        }

        String lexeme = text.substring(mark, mark + bufSize);
        return new Token(TokenKind.HEX_LITERAL, lexeme);
    }

    private Token scanHex() {
        int mark = pos;
        int bufSize = 1;

        scanChar();

        while (true) {
            ch = charAt(++pos);

            if (!isHex(ch)) {
                break;
            }

            bufSize++;
        }

        String lexeme = text.substring(mark, mark + bufSize);
        return new Token(TokenKind.HEX_LITERAL, lexeme);
    }

    public static void main(String[] args) {
        // TODO
        // BINARY_LITERAL:     '0' [bB] [01] ([01_]* [01])? [lL]?;
        System.out.println(0b11_01);
        System.out.println(0B10_01L);
    }


//    public void scanNumber() {
//        mark = pos;
//        numberSale = 0;
//        numberExp = false;
//        bufPos = 0;
//
//        if (ch == '0' && charAt(pos + 1) == 'b' && dbType != DbType.odps) {
//            int i = 2;
//            int mark = pos + 2;
//            for (;;++i) {
//                char ch = charAt(pos + i);
//                if (ch == '0' || ch == '1') {
//                    continue;
//                } else if (ch >= '2' && ch <= '9') {
//                    break;
//                } else {
//                    bufPos += i;
//                    pos += i;
//                    stringVal = subString(mark, i - 2);
//                    this.ch = charAt(pos);
//                    token = Token.BITS;
//                    return;
//                }
//            }
//        }
//
//        if (ch == '-') {
//            bufPos++;
//            ch = charAt(++pos);
//        }
//
//        for (;;) {
//            if (ch >= '0' && ch <= '9') {
//                bufPos++;
//            } else {
//                break;
//            }
//            ch = charAt(++pos);
//        }
//
//        if (ch == '.') {
//            if (charAt(pos + 1) == '.') {
//                token = Token.LITERAL_INT;
//                return;
//            }
//            bufPos++;
//            ch = charAt(++pos);
//
//            for (numberSale = 0;;numberSale++) {
//                if (ch >= '0' && ch <= '9') {
//                    bufPos++;
//                } else {
//                    break;
//                }
//                ch = charAt(++pos);
//            }
//
//            numberExp = true;
//        }
//
//        if ((ch == 'e' || ch == 'E')
//                && (isDigit(charAt(pos + 1)) || (isDigit2(charAt(pos + 1)) && isDigit2(charAt(pos + 2))))) {
//            numberExp = true;
//
//            bufPos++;
//            ch = charAt(++pos);
//
//            if (ch == '+' || ch == '-') {
//                bufPos++;
//                ch = charAt(++pos);
//            }
//
//            for (;;) {
//                if (ch >= '0' && ch <= '9') {
//                    bufPos++;
//                } else {
//                    break;
//                }
//                ch = charAt(++pos);
//            }
//
//            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
//                numberExp = false;
//            }
//        }
//
//        if (numberSale > 0 || numberExp) {
//            if (text.charAt(mark) == '.' && isIdentifierChar(ch)) {
//                pos = mark + 1;
//                ch = charAt(pos);
//                token = Token.DOT;
//                return;
//            }
//            token = Token.LITERAL_FLOAT;
//            return;
//        }
//
//        if (ch != '`') {
//            if (isFirstIdentifierChar(ch)
//                    && ch != '）'
//                    && !(ch == 'b' && bufPos == 1 && charAt(pos - 1) == '0' && dbType != DbType.odps)
//            ) {
//                bufPos++;
//                boolean brace = false;
//                for (;;) {
//                    char c0 = ch;
//                    ch = charAt(++pos);
//
//                    if (isEOF()) {
//                        break;
//                    }
//
//                    if (!isIdentifierChar(ch)) {
//                        if (ch == '{' && charAt(pos - 1) == '$' && !brace) {
//                            bufPos++;
//                            brace = true;
//                            continue;
//                        }
//
//                        if (ch == '}' && brace) {
//                            bufPos++;
//                            brace = false;
//                            continue;
//                        }
//
//                        if ((ch == '（'  || ch == '）')
//                                && c0 > 256) {
//                            bufPos++;
//                            continue;
//                        }
//                        break;
//                    }
//
//                    bufPos++;
//                    continue;
//                }
//
//                stringVal = addSymbol();
//                hash_lower = FnvHash.hashCode64(stringVal);
//                token = Token.IDENTIFIER;
//                return;
//            }
//        }
//
//        token = Token.LITERAL_INT;
//    }

    private Token scanId() {
        int mark = pos;
        int bufSize = 1;

        while (true) {
            ch = charAt(++pos);

            if (!isIdChar(ch)) {
                break;
            }

            bufSize++;
        }

        String lexeme = text.substring(mark, mark + bufSize);

        if (Token.isKeyword(lexeme)) {
            TokenKind kind = Token.kind(lexeme);
            return new Token(kind, kind.literal);
        } else {
            return new Token(TokenKind.IDENTIFIER, lexeme);
        }
    }

    //

//            if self.current_char == '{':
//            self.advance()
//            self.skip_comment()
//            continue

//            if self.current_char == ':' and self.peek() == '=':
//            self.advance()
//            self.advance()
//            return Token(ASSIGN, ':=')
//            if self.current_char == '.':
//            self.advance()
//            return Token(DOT, '.')
//
//            self.error()
//
//                    return Token(EOF, None)

    private char charAt(int index) {
        if (index >= text.length()) {
            return CharUtils.EOF;
        }

        return text.charAt(index);
    }
}
