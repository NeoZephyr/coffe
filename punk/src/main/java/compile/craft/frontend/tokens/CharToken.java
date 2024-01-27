package compile.craft.frontend.tokens;

import compile.craft.frontend.Source;
import compile.craft.frontend.Token;
import compile.craft.frontend.TokenKind;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import static compile.craft.frontend.ErrorCode.INVALID_CHARACTER;
import static compile.craft.frontend.ErrorCode.UNCLOSED_CHARACTER;

public class CharToken extends Token {

    public CharToken(Source source) {
        super(source);
    }

    @SneakyThrows
    @Override
    protected void extract() {
        StringBuilder sb = new StringBuilder();
        sb.append(source.current());
        char c = source.advance();

        if (c == '\'' || c == '\r' || c == '\n') {
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

            sb.append(sequence);
            value = StringEscapeUtils.unescapeJava(sequence).charAt(0);
        } else {
            sb.append(c);
            value = c;
            source.advance();
        }

        if (source.current() != '\'') {
            kind = TokenKind.ERROR;
            value = UNCLOSED_CHARACTER;
            return;
        } else {
            sb.append(source.current());
            source.advance(); // consume closed quotes
            kind = TokenKind.CHAR_LITERAL;
        }

        text = sb.toString();
    }
}
