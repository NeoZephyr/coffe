package compile.craft;

public interface TokenStream {

    Token read();

    Token peek(int offset);

    boolean seek(int offset);
}
