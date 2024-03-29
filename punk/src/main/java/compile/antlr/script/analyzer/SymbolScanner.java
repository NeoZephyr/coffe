package compile.antlr.script.analyzer;

import compile.antlr.script.ScriptBaseListener;
import compile.antlr.script.ScriptParser;
import compile.antlr.script.symbol.*;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Stack;

/**
 * 第一遍扫描：识别出所有类型（包括类和函数)，以及 Scope
 */
public class SymbolScanner extends ScriptBaseListener {
    private AnnotatedTree tree = null;
    private Stack<Scope> scopes = new Stack<>();

    public SymbolScanner(AnnotatedTree tree) {
        this.tree = tree;
    }

    @Override
    public void enterProg(ScriptParser.ProgContext ctx) {
        Namespace scope = new Namespace("", currentScope(), ctx);
        tree.namespace = scope;
        pushScope(scope, ctx);
    }

    @Override
    public void exitProg(ScriptParser.ProgContext ctx) {
        popScope();
    }

    @Override
    public void enterBlock(ScriptParser.BlockContext ctx) {

        // 对于函数，不需要再额外建一个 scope
        if (!(ctx.parent instanceof ScriptParser.FunctionBodyContext)) {
            Block scope = new Block(currentScope(), ctx);
            currentScope().addSymbol(scope);
            pushScope(scope, ctx);
        }
    }

    @Override
    public void exitBlock(ScriptParser.BlockContext ctx) {
        if (!(ctx.parent instanceof ScriptParser.FunctionBodyContext)) {
            popScope();
        }
    }

    @Override
    public void enterStatement(ScriptParser.StatementContext ctx) {
        if (ctx.FOR() != null) {
            Block scope = new Block(currentScope(), ctx);
            currentScope().addSymbol(scope);
            pushScope(scope, ctx);
        }
    }

    @Override
    public void exitStatement(ScriptParser.StatementContext ctx) {
        if (ctx.FOR() != null) {
            popScope();
        }
    }

    @Override
    public void enterFunctionDeclaration(ScriptParser.FunctionDeclarationContext ctx) {
        String name = ctx.IDENTIFIER().getText();

        // 目前 function 的信息并不完整
        Function function = new Function(name, currentScope(), ctx);
        tree.types.add(function);
        currentScope().addSymbol(function);
        pushScope(function, ctx);
    }

    @Override
    public void exitFunctionDeclaration(ScriptParser.FunctionDeclarationContext ctx) {
        popScope();
    }

    @Override
    public void enterClassDeclaration(ScriptParser.ClassDeclarationContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        Klass klass = new Klass(name, ctx);
        tree.types.add(klass);

        // 只是报警，但仍然继续解析
        if (tree.lookupKlass(currentScope(), name) != null) {
            tree.error("duplicate class name:" + name, ctx);
        }

        currentScope().addSymbol(klass);
        pushScope(klass, ctx);
    }

    @Override
    public void exitClassDeclaration(ScriptParser.ClassDeclarationContext ctx) {
        popScope();
    }

    private Scope pushScope(Scope scope, ParserRuleContext ctx) {
        tree.nodeToScope.put(ctx, scope);
        scope.ctx = ctx;
        scopes.push(scope);
        return scope;
    }

    private void popScope() {
        scopes.pop();
    }

    private Scope currentScope() {
        if (scopes.empty()) {
            return null;
        } else {
            return scopes.peek();
        }
    }
}