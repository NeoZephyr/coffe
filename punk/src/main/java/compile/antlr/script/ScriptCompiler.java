package compile.antlr.script;

import compile.antlr.script.listener.*;
import compile.antlr.script.visitor.ASTEvaluator;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * 词法分析、语法分析、语义分析
 */
public class ScriptCompiler {

    private AnnotatedTree tree;
    private ScriptParser parser;

    public AnnotatedTree compile(String script, boolean verbose, boolean astDump) {
        tree = new AnnotatedTree();
        ScriptLexer lexer = new ScriptLexer(CharStreams.fromString(script));
        CommonTokenStream stream = new CommonTokenStream(lexer);
        parser = new ScriptParser(stream);
        tree.ast = parser.prog();
        ParseTreeWalker walker = new ParseTreeWalker();

        // 解析类型、Scope
        TypeAndScopeScanner scanner = new TypeAndScopeScanner(tree);
        walker.walk(scanner, tree.ast);

        // 把变量、类继承、函数声明的类型都解析出来
        TypeResolver typeResolver = new TypeResolver(tree);
        walker.walk(typeResolver, tree.ast);

        // 消解有的变量应用、函数引用。进行类型的推断
        RefResolver refResolver = new RefResolver(tree);
        walker.walk(refResolver, tree.ast);

        TypeChecker typeChecker = new TypeChecker(tree);
        walker.walk(typeChecker, tree.ast);

        SemanticValidator semanticValidator = new SemanticValidator(tree);
        walker.walk(semanticValidator, tree.ast);

        ClosureAnalyzer analyzer = new ClosureAnalyzer(tree);
        analyzer.analyzeClosures();

        if (verbose || astDump) {
            dumpAst();
        }

        if (verbose) {
            dumpSymbols();
        }

        return tree;
    }

    public AnnotatedTree compile(String script) {
        return compile(script, false, false);
    }

    public void dumpSymbols() {
        if (tree != null) {
            System.out.println(tree.getScopeTreeString());
        }
    }

    public void dumpAst() {
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
        Object result = visitor.visit(tree.ast);
        return result;
    }
}