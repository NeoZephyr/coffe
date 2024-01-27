package compile.craft.frontend.tokens;

import compile.craft.frontend.ErrorCode;
import compile.craft.frontend.Source;
import compile.craft.frontend.Token;
import compile.craft.frontend.TokenKind;

public class ErrorToken extends Token {

    public ErrorToken(Source source, ErrorCode errorCode, String text) {
        super(source);
        this.text = text;
        this.kind = TokenKind.ERROR;
        this.value = errorCode;
    }

    @Override
    protected void extract() {}
}
