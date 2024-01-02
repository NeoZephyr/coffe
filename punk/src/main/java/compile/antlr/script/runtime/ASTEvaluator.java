package compile.antlr.script.runtime;

import compile.antlr.script.ScriptBaseVisitor;
import compile.antlr.script.analyzer.AnnotatedTree;

public class ASTEvaluator extends ScriptBaseVisitor<Object> {

    public AnnotatedTree tree = null;

    public ASTEvaluator(AnnotatedTree tree) {
        this.tree = tree;
    }
}
