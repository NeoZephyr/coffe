package compile.craft.frontend.tokens;

import compile.craft.frontend.Source;
import compile.craft.frontend.Token;

import static compile.craft.frontend.TokenKind.END_OF_FILE;

public class EofToken extends Token {

    public EofToken(Source source) {
        super(source);
        this.kind = END_OF_FILE;
    }

    @Override
    protected void extract() {}
}
