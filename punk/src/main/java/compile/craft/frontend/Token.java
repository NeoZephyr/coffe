package compile.craft.frontend;

import lombok.Data;

import java.io.IOException;

@Data
public abstract class Token {

    public TokenKind kind;
    public String text;
    public Object value;
    int lineno;
    int column;
    int length;

    public Source source;

    public Token(Source source) {
        this.source = source;
        this.lineno = source.lineno;
        this.column = source.column;
        extract();
        this.length = text.length();
    }

    protected abstract void extract();

    protected String escapeSequence() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(source.current());
        char c = source.advance();

        if (c == 'u') {
            while (source.current() == 'u') {
                sb.append('u');
                source.advance();
            }

            char c1 = source.current();
            char c2 = source.peek(1);
            char c3 = source.peek(2);
            char c4 = source.peek(3);

            if (CharUtils.isHex(c1) && CharUtils.isHex(c2) && CharUtils.isHex(c3) && CharUtils.isHex(c4)) {
                source.advance();
                source.advance();
                source.advance();
                sb.append(c1);
                sb.append(c2);
                sb.append(c3);
                sb.append(c4);
                return sb.toString();
            }

            return null;
        }

        if (c == 'b' || c == 't' || c == 'n' || c == 'f' || c == 'r' || c == '"' || c == '\'' || c == '\\') {
            sb.append(c);
            return sb.toString();
        }

        return null;
    }
}
