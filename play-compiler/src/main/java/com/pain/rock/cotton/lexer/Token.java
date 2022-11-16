package com.pain.rock.cotton.lexer;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Token {

    enum Kind {
        VOID("void"),
        BOOLEAN("boolean"),
        BYTE("byte"),
        CHAR("char"),
        SHORT("short"),
        INT("int"),
        LONG("long"),
        DOUBLE("double"),
        FLOAT("float"),
        STRING("string"),
        CONST("const"),
        FINAL("final"),
        TRANSIENT("transient"),
        SYNCHRONIZED("synchronized"),
        VOLATILE("volatile"),
        STATIC("static"),
        NATIVE("native"),
        DEFAULT("default"),
        ABSTRACT("abstract"),
        THIS("this"),
        SUPER("super"),
        STRICTFP("strictfp"),
        NEW("new"),
        ASSERT("assert"),
        INSTANCEOF("instanceof"),
        THROW("throw"),
        THROWS("throws"),
        FUNCTION("function"),
        INTERFACE("interface"),
        CLASS("class"),
        ENUM("enum"),
        EXTENDS("extends"),
        IMPLEMENTS("implements"),
        PACKAGE("package"),
        PRIVATE("private"),
        PROTECTED("protected"),
        PUBLIC("public"),
        IMPORT("import"),
        IF("if"),
        ELSE("else"),
        FOR("for"),
        SWITCH("switch"),
        CASE("case"),
        BREAK("break"),
        CONTINUE("continue"),
        DO("do"),
        WHILE("while"),
        TRY("try"),
        CATCH("catch"),
        FINALLY("finally"),
        RETURN("return"),
        GOTO("goto"),
        BOOL_LITERAL,
        DECIMAL_LITERAL,
        HEX_LITERAL,
        OCT_LITERAL,
        BINARY_LITERAL,
        FLOAT_LITERAL,
        HEX_FLOAT_LITERAL,
        CHAR_LITERAL,
        STRING_LITERAL,
        NULL_LITERAL("null"),
        IDENTIFIER,
        LPAREN("("),
        RPAREN(")"),
        LBRACE("{"),
        RBRACE("}"),
        LBRACK("["),
        RBRACK("]"),
        SEMI(";"),
        COMMA(","),
        DOT("."),
        ASSIGN("="),
        GT(">"),
        LT("<"),
        BANG("!"),
        TILDE("~"),
        QUESTION("?"),
        COLON(":"),
        EQUAL("=="),
        LE("<="),
        GE(">="),
        NOTEQUAL("!="),
        AND("&&"),
        OR("||"),
        INC("++"),
        DEC("--"),
        ADD("+"),
        SUB("-"),
        MUL("*"),
        DIV("/"),
        BITAND("&"),
        BITOR("|"),
        CARET("^"),
        MOD("%"),
        ADD_ASSIGN("+="),
        SUB_ASSIGN("-="),
        MUL_ASSIGN("*="),
        DIV_ASSIGN("/="),
        AND_ASSIGN("&="),
        OR_ASSIGN("|="),
        XOR_ASSIGN("^="),
        MOD_ASSIGN("%="),
        LSHIFT_ASSIGN("<<="),
        RSHIFT_ASSIGN(">>="),
        URSHIFT_ASSIGN(">>>="),
        ARROW("->"),
        COLONCOLON("::"),
        AT("@"),
        ELLIPSIS("..."),
        ;

        public String literal;

        Kind() {
            this(null);
        }

        Kind(String literal) {
            this.literal = literal;
        }
    }

    private static Map<String, Kind> keywords = new HashMap<>();

    static {
        Kind[] kinds = Kind.values();

        for (Kind kind : kinds) {
            if (kind.literal != null) {
                keywords.put(kind.literal, kind);
            }
        }
    }

    Kind kind;
    String text;

    public Token(Kind kind, String text) {
        this.kind = kind;
        this.text = text;
    }

    public static boolean isKeyword(String lexeme) {
        return keywords.containsKey(lexeme);
    }

    public static Kind kind(String lexeme) {
        return keywords.get(lexeme);
    }
}
