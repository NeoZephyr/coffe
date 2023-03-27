package compile.craft;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Token {

    private static Map<String, TokenKind> keywords = new HashMap<>();

    static {
        TokenKind[] kinds = TokenKind.values();

        for (TokenKind kind : kinds) {
            if (kind.literal != null) {
                keywords.put(kind.literal, kind);
            }
        }
    }

    TokenKind kind;
    String lexeme;

    // int line;
    int position;
    int length;

    public Token(TokenKind kind, String lexeme, int position) {
        this.kind = kind;
        this.lexeme = lexeme;
        this.position = position;
        this.length = lexeme.length();
    }

    public Token(TokenKind kind, String lexeme) {
        this.kind = kind;
        this.lexeme = lexeme;
    }

    public static boolean isKeyword(String lexeme) {
        return keywords.containsKey(lexeme);
    }

    public static TokenKind kind(String lexeme) {
        return keywords.get(lexeme);
    }
}
