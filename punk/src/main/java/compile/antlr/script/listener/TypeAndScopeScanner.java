package compile.antlr.script.listener;

import compile.antlr.script.AnnotatedTree;
import compile.antlr.script.ScriptBaseListener;
import compile.antlr.script.ScriptParser;
import compile.antlr.script.symbol.*;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Stack;

/**
 * 识别出所有类型（包括类和函数)，以及 Scope
 */
public class TypeAndScopeScanner extends ScriptBaseListener {

    private AnnotatedTree tree;
    private Stack<Scope> scopes = new Stack<>();

    public TypeAndScopeScanner(AnnotatedTree tree) {
        this.tree = tree;
    }

    private Scope pushScope(Scope scope, ParserRuleContext ctx) {
        tree.nodeToScope.put(ctx, scope);
        scope.setCtx(ctx);
        scopes.push(scope);
        return scope;
    }

    private void popScope() {
        scopes.pop();
    }

    private Scope currentScope() {
        if (scopes.size() > 0) {
            return scopes.peek();
        } else {
            return null;
        }
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
        // 为 for 建立额外的 Scope
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
        String idName = ctx.IDENTIFIER().getText();

        // 目前 function 信息并不完整，参数要等到 TypeResolver.java 中去确定
        Function func = new Function(idName, currentScope(), ctx);
        tree.addType(func);
        currentScope().addSymbol(func);
        pushScope(func, ctx);
    }

    @Override
    public void exitFunctionDeclaration(ScriptParser.FunctionDeclarationContext ctx) {
        popScope();
    }

    @Override
    public void enterClassDeclaration(ScriptParser.ClassDeclarationContext ctx) {
        String idName = ctx.IDENTIFIER().getText();
        Klass klass = new Klass(idName, ctx);
        tree.addType(klass);

        if (tree.lookupKlass(currentScope(), idName) != null) {
            tree.log("duplicate class name: " + idName, ctx);
        }

        currentScope().addSymbol(klass);
        pushScope(klass, ctx);
    }

    @Override
    public void exitClassDeclaration(ScriptParser.ClassDeclarationContext ctx) {
        popScope();
    }
}