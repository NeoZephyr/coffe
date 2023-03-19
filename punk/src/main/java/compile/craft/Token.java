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
    String text;
    int position;
    int length;

    public Token(TokenKind kind, String text, int position) {
        this.kind = kind;
        this.text = text;
        this.position = position;
        this.length = text.length();
    }

    public Token(TokenKind kind, String text) {
        this.kind = kind;
        this.text = text;
    }

    public static boolean isKeyword(String lexeme) {
        return keywords.containsKey(lexeme);
    }

    public static TokenKind kind(String lexeme) {
        return keywords.get(lexeme);
    }
}
