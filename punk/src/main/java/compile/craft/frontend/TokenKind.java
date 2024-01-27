package compile.craft.frontend;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * 保留字、数据类型、运算符、标识
 */
public enum TokenKind {

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
    NULL_LITERAL("null"),

    BOOL_LITERAL,
    DECIMAL_LITERAL,
    HEX_LITERAL,
    OCT_LITERAL,
    BINARY_LITERAL,
    FLOAT_LITERAL,
    HEX_FLOAT_LITERAL,
    CHAR_LITERAL,
    STRING_LITERAL,
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

    END_OF_FILE,
    ERROR
    ;

    public static final Map<String, TokenKind> RESERVED_WORDS = new HashMap<>();
    public static final Map<String, TokenKind> SPECIAL_SYMBOL_WORDS = new HashMap<>();
    public static final Map<Character, Integer> SPECIAL_SYMBOL_HEADERS = new HashMap<>();
    public static final int START_RESERVED_IDX = VOID.ordinal();
    public static final int END_RESERVED_IDX = NULL_LITERAL.ordinal();
    public static final int START_SPECIAL_SYMBOL_IDX = LPAREN.ordinal();
    public static final int END_SPECIAL_SYMBOL_IDX = ELLIPSIS.ordinal();

    static {
        TokenKind[] kinds = TokenKind.values();

        for (int i = START_RESERVED_IDX; i <= END_RESERVED_IDX; i++) {
            RESERVED_WORDS.put(kinds[i].text, kinds[i]);
        }

        for (int i = START_SPECIAL_SYMBOL_IDX; i < END_SPECIAL_SYMBOL_IDX; i++) {
            SPECIAL_SYMBOL_WORDS.put(kinds[i].text, kinds[i]);

            char header = kinds[i].text.charAt(0);
            Integer times = SPECIAL_SYMBOL_HEADERS.getOrDefault(header, 0);
            SPECIAL_SYMBOL_HEADERS.put(header, times + 1);
        }
    }

    public final String text;

    TokenKind() {
        this.text = toString().toLowerCase();
    }

    TokenKind(String literal) {
        this.text = literal;
    }
}
