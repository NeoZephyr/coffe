package compile.antlr.script.runtime;

import compile.antlr.script.ScriptLexer;
import compile.antlr.script.ScriptParser;
import compile.antlr.script.analyzer.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class ScriptCompiler {
    AnnotatedTree tree = null;
    ScriptLexer lexer = null;
    ScriptParser parser = null;

    public AnnotatedTree compile(String script, boolean verbose) {
        tree = new AnnotatedTree();
        lexer = new ScriptLexer(CharStreams.fromString(script));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        parser = new ScriptParser(tokens);
        // parser.addErrorListener(null);
        tree.ast = parser.prog();
        ParseTreeWalker walker = new ParseTreeWalker();

        SymbolScanner scanner = new SymbolScanner(tree);
        walker.walk(scanner, tree.ast);

        TypeResolver typeResolver = new TypeResolver(tree);
        walker.walk(typeResolver, tree.ast);

        RefResolver refResolver = new RefResolver(tree);
        walker.walk(refResolver, tree.ast);

        TypeChecker typeChecker = new TypeChecker(tree);
        walker.walk(typeChecker, tree.ast);

        SemanticValidator semanticValidator = new SemanticValidator(tree);
        walker.walk(semanticValidator, tree.ast);

        ClosureAnalyzer closureAnalyzer = new ClosureAnalyzer(tree);
        closureAnalyzer.analyze();

        if (verbose) {
            dumpAST();
            dumpSymbols();
        }

        return tree;
    }

    public AnnotatedTree compile(String script) {
        return compile(script, false);
    }

    public void dumpSymbols() {
        if (tree != null) {
            System.out.println(tree.getScopeTreeText());
        }
    }

    public void dumpAST() {
        if (tree != null) {
            System.out.println(tree.ast.toStringTree(parser));
        }
    }

    public void dumpCompilationLogs() {
        if (tree != null) {
            for (CompilationLog log : tree.logs) {
                System.out.println(log);
            }
        }
    }

    public Object execute(AnnotatedTree tree) {
        ASTEvaluator visitor = new ASTEvaluator(tree);
        return visitor.visit(tree.ast);
    }

}