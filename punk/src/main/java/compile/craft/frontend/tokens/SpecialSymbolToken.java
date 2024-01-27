package compile.craft.frontend.tokens;

import compile.craft.frontend.Source;
import compile.craft.frontend.Token;
import compile.craft.frontend.TokenKind;
import lombok.SneakyThrows;

import static compile.craft.frontend.ErrorCode.INVALID_CHARACTER;

public class SpecialSymbolToken extends Token {
    public SpecialSymbolToken(Source source) {
        super(source);
    }

    @SneakyThrows
    @Override
    protected void extract() {
        char c = source.current();
        text = String.valueOf(c);

        if (TokenKind.SPECIAL_SYMBOL_HEADERS.get(c) == 1) {
            source.advance(); // consume character
            kind = TokenKind.SPECIAL_SYMBOL_WORDS.get(text);
            return;
        }

        switch (c) {
            case '<':
                c = source.advance();

                if (c == '=') {
                    text += c;
                    source.advance();
                } else if (c == '<' && source.peek() == '=') {
                    text += c;
                    text += source.advance();
                    source.advance();
                }
                break;
            case '>':
                c = source.advance();

                if (c == '=') {
                    text += c;
                    source.advance();
                } else if (c == '>') {
                    if (source.peek() == '=') {
                        text += c;
                        text += source.advance();
                        source.advance();
                    } else if (source.peek() == '>' && source.peek() == '=') {
                        text += c;
                        text += source.advance();
                        text += source.advance();
                        source.advance();
                    }
                }
                break;
            case '&':
                c = source.advance();

                if (c == '&' || c == '=') {
                    text += c;
                    source.advance();
                }
                break;
            case '|':
                c = source.advance();

                if (c == '|' || c == '=') {
                    text += c;
                    source.advance();
                }
                break;
            case '+':
                c = source.advance();

                if (c == '+' || c == '=') {
                    text += c;
                    source.advance();
                }
                break;
            case '-':
                c = source.advance();

                if (c == '-') {
                    text += c;
                    source.advance();
                } else if (c == '=') {
                    text += c;
                    source.advance();
                } else if (c == '>') {
                    text += c;
                    source.advance();
                }
                break;
            case '=':
            case '!':
            case '^':
            case '*':
            case '/':
            case '%':
                c = source.advance();

                if (c == '=') {
                    text += c;
                    source.advance();
                }
                break;
            case ':':
                c = source.advance();

                if (c == ':') {
                    text += c;
                    source.advance();
                }
                break;
            case '.':
                c = source.advance();

                if (c == '.' && source.peek() == '.') {
                    text += c;
                    text += c;
                    source.advance();
                    source.advance();
                }
                break;
            default:
                source.advance();
                kind = TokenKind.ERROR;
                value = INVALID_CHARACTER;
        }

        if (kind == null) {
            kind = TokenKind.SPECIAL_SYMBOL_WORDS.get(text);
        }
    }
}
