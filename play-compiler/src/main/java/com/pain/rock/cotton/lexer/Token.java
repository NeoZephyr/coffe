package com.pain.rock.cotton.lexer;

import lombok.Data;

@Data
public class Token {

    public static final int
            VOID = 1, BOOLEAN = 2, BYTE = 3, CHAR = 4, SHORT = 5, INT = 6, LONG = 7, DOUBLE = 8, FLOAT = 9,
            STRING = 10, CONST = 11, FINAL = 12, TRANSIENT = 13, SYNCHRONIZED = 14, VOLATILE = 15,
            STATIC = 16, NATIVE = 17, DEFAULT = 18, ABSTRACT = 19, THIS = 20, SUPER = 21, STRICTFP = 22,
            NEW = 23, ASSERT = 24, INSTANCEOF = 25, THROW = 26, THROWS = 27, FUNCTION = 28, INTERFACE = 29,
            CLASS = 30, ENUM = 31, EXTENDS = 32, IMPLEMENTS = 33, PACKAGE = 34, PRIVATE = 35,
            PROTECTED = 36, PUBLIC = 37, IMPORT = 38, IF = 39, ELSE = 40, FOR = 41, SWITCH = 42,
            CASE = 43, BREAK = 44, CONTINUE = 45, DO = 46, WHILE = 47, TRY = 48, CATCH = 49, FINALLY = 50,
            RETURN = 51, GOTO = 52, BOOL_LITERAL = 53, DECIMAL_LITERAL = 54, HEX_LITERAL = 55,
            OCT_LITERAL = 56, BINARY_LITERAL = 57, FLOAT_LITERAL = 58, HEX_FLOAT_LITERAL = 59,
            CHAR_LITERAL = 60, STRING_LITERAL = 61, NULL_LITERAL = 62, IDENTIFIER = 63, LPAREN = 64,
            RPAREN = 65, LBRACE = 66, RBRACE = 67, LBRACK = 68, RBRACK = 69, SEMI = 70, COMMA = 71,
            DOT = 72, ASSIGN = 73, GT = 74, LT = 75, BANG = 76, TILDE = 77, QUESTION = 78, COLON = 79,
            EQUAL = 80, LE = 81, GE = 82, NOTEQUAL = 83, AND = 84, OR = 85, INC = 86, DEC = 87, ADD = 88,
            SUB = 89, MUL = 90, DIV = 91, BITAND = 92, BITOR = 93, CARET = 94, MOD = 95, ADD_ASSIGN = 96,
            SUB_ASSIGN = 97, MUL_ASSIGN = 98, DIV_ASSIGN = 99, AND_ASSIGN = 100, OR_ASSIGN = 101,
            XOR_ASSIGN = 102, MOD_ASSIGN = 103, LSHIFT_ASSIGN = 104, RSHIFT_ASSIGN = 105,
            URSHIFT_ASSIGN = 106, ARROW = 107, COLONCOLON = 108, AT = 109, ELLIPSIS = 110, WS = 111,
            COMMENT = 112, LINE_COMMENT = 113;

    int type;
    String value;

    public Token(int type, String value) {
        this.type = type;
        this.value = value;
    }
}
