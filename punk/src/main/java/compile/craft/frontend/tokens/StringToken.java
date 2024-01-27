package compile.craft.frontend.tokens;

import compile.craft.frontend.Source;
import compile.craft.frontend.Token;
import compile.craft.frontend.TokenKind;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import static compile.craft.frontend.CharUtils.EOF;
import static compile.craft.frontend.ErrorCode.*;

public class StringToken extends Token {

    public StringToken(Source source) {
        super(source);
    }

    @SneakyThrows
    @Override
    protected void extract() {
        StringBuilder textSb = new StringBuilder();
        StringBuilder valueSb = new StringBuilder();
        textSb.append(source.current());

        while (true) {
            char c = source.advance();

            if (c == '\"' || c == EOF) {
                break;
            }

            if (c == '\r' || c == '\n') {
                kind = TokenKind.ERROR;
                value = INVALID_CHARACTER;
                return;
            }

            if (c == '\\') {
                String sequence = escapeSequence();

                if (StringUtils.isEmpty(sequence)) {
                    kind = TokenKind.ERROR;
                    value = INVALID_CHARACTER;
                    return;
                }

                textSb.append(sequence);
                valueSb.append(sequence);
            } else {
                textSb.append(c);
                valueSb.append(c);
            }
        }

        if (source.current() == '\"') {
            textSb.append(source.current());
            source.advance(); // consume closed quotes
            kind = TokenKind.STRING_LITERAL;
            value = valueSb.toString();
        } else {
            kind = TokenKind.ERROR;
            value = UNCLOSED_STRING;
        }

        text = textSb.toString();
    }
}
