package compile.antlr.expr;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;

public class ExprTest {
    public static void main(String[] args) throws IOException {
        CharStream inputStream = CharStreams.fromFileName("input/antlr/expr.txt");
        ExprLexer lexer = new ExprLexer(inputStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ExprParser parser = new ExprParser(tokens);
        ExprParser.ProgContext context = parser.prog();

        EvalVisitor visitor = new EvalVisitor();
        visitor.visit(context);
    }
}
