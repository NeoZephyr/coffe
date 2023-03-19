package compile.easy.lexer;

public interface Token {
    TokenType getType();
    String getText();
}
