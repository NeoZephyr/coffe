package compile.craft;

public class GrammarTest {

    public static void main(String[] args) {
        Lexer lexer = new Lexer("0B0");
        Token token = lexer.nextToken();
        System.out.println(token);

        System.out.println(.0e0);
        System.out.println(.0e0);
    }
}
