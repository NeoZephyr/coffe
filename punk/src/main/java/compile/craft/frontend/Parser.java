package compile.craft.frontend;

import compile.craft.intermediate.SymbolTable;
import lombok.SneakyThrows;

import java.io.IOException;

public class Parser {
    public Lexer lexer;
    public Token[] lookahead;
    public int k;
    public int p;

    public ErrorHandler errorHandler = new ErrorHandler();
    public SymbolTable symbolTable;

    public Parser(Lexer lexer, int k) throws IOException {
        this.lexer = lexer;
        this.k = k;
        lookahead = new Token[k];

        for (int i = 0; i < k; i++) {
            advance();
        }
    }

    public void parse() {
        try {
            Token token = lexer.nextToken();

            if (token.kind == TokenKind.ERROR) {
                errorHandler.flag(token, (ErrorCode) token.getValue());
            }
        } catch (IOException e) {
            errorHandler.abort(ErrorCode.IO_ERROR);
        }
        // send parse summary message
    }

    public void match(TokenKind kind) throws IOException {
        if (LA(1) == kind) {
            advance();
        } else {
            // error();
        }
    }

    public Token LT(int i) {
        return lookahead[(p + i - 1) % k];
    }

    public TokenKind LA(int i) {
        return LT(i).kind;
    }

    public void advance() throws IOException {
        lookahead[p] = lexer.nextToken();
        p = (p + 1) % k;
    }
}
