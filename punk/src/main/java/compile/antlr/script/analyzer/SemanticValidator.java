package compile.antlr.script.analyzer;

import compile.antlr.script.ScriptBaseListener;
import compile.antlr.script.ScriptParser;
import compile.antlr.script.symbol.Function;
import compile.antlr.script.types.Type;
import compile.antlr.script.types.VoidType;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * 1. break 只能出现在循环语句中，或 case 语句中
 *
 * 2. return 语句
 *  2.1 函数声明了返回值，就一定要有 return 语句。除非返回值类型是 void
 *  2.2 类的构造函数里如果用到 return，不能带返回值
 *  2.3 return 语句只能出现在函数里
 *  2.4 返回值类型检查 -> 在 TypeChecker 里做
 *
 * 3. 左值
 *  3.1 标注左值（不标注就是右值）
 *  3.2 检查表达式能否生成合格的左值
 *
 * 4. 类的声明不能在函数里（应该也可以，只不过对生存期有要求）
 *
 * 5. super() 和 this()，只能是构造函数中的第一句。这个在 RefResolver 中实现了
 */
public class SemanticValidator extends ScriptBaseListener {

    public AnnotatedTree tree = null;

    public SemanticValidator(AnnotatedTree tree) {
        this.tree = tree;
    }

    @Override
    public void exitPrimary(ScriptParser.PrimaryContext ctx) {}

    @Override
    public void exitFunctionCall(ScriptParser.FunctionCallContext ctx) {}

    @Override
    public void exitExpression(ScriptParser.ExpressionContext ctx) {}

    @Override
    public void exitClassDeclaration(ScriptParser.ClassDeclarationContext ctx) {
        if (tree.enclosingFunctionOfNode(ctx) != null) {
            tree.error("can not declare class inside function", ctx);
        }
    }

    @Override
    public void exitFunctionDeclaration(ScriptParser.FunctionDeclarationContext ctx) {
        if (ctx.typeOrVoid() != null) {
            if (!hasReturnStatement(ctx)) {
                Type returnType = tree.nodeToType.get(ctx.typeOrVoid());

                if (!(returnType == VoidType.instance())) {
                    tree.error("return statement expected in function", ctx);
                }
            }
        }
    }

    @Override
    public void exitVariableDeclarators(ScriptParser.VariableDeclaratorsContext ctx) {
        super.exitVariableDeclarators(ctx);
    }

    @Override
    public void exitVariableDeclarator(ScriptParser.VariableDeclaratorContext ctx) {
        super.exitVariableDeclarator(ctx);
    }

    @Override
    public void exitVariableDeclaratorId(ScriptParser.VariableDeclaratorIdContext ctx) {
        super.exitVariableDeclaratorId(ctx);
    }

    /**
     * 对变量初始化部分进行类型推断
     */
    @Override
    public void exitVariableInitializer(ScriptParser.VariableInitializerContext ctx) {}

    /**
     * 根据字面量来推断类型
     */
    @Override
    public void exitLiteral(ScriptParser.LiteralContext ctx) {}

    @Override
    public void exitStatement(ScriptParser.StatementContext ctx) {
        if (ctx.RETURN() != null) {
            Function function = tree.enclosingFunctionOfNode(ctx);

            if (function == null) {
                tree.error("return statement not in function body", ctx);
            } else if (function.isConstructor() && (ctx.expression() != null)) {
                tree.error("can not return a value from constructor", ctx);
            }
        } else if (ctx.BREAK() != null) {
            if (!checkBreak(ctx)) {
                tree.error("break statement not in loop or switch statements", ctx);
            }
        }
    }

    private boolean hasReturnStatement(ParseTree ctx) {
        for (int i = 0; i < ctx.getChildCount(); ++i) {
            ParseTree child = ctx.getChild(i);

            if ((child instanceof ScriptParser.StatementContext) &&
                    (((ScriptParser.StatementContext) child).RETURN() != null)) {
                return true;
            } else if (!((child instanceof ScriptParser.FunctionDeclarationContext) || (child instanceof ScriptParser.ClassDeclarationContext))) {
                boolean hasReturn = hasReturnStatement(child);

                if (hasReturn) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkBreak(RuleContext ctx) {
        if ((ctx.parent instanceof ScriptParser.StatementContext) &&
                ((((ScriptParser.StatementContext) ctx.parent).FOR() != null) || (((ScriptParser.StatementContext) ctx.parent).WHILE() != null)) ||
                (ctx.parent instanceof ScriptParser.SwitchBlockStatementGroupContext)) {
            return true;
        } else if ((ctx.parent == null) || (ctx.parent instanceof ScriptParser.FunctionDeclarationContext)) {
            return false;
        } else {
            return checkBreak(ctx.parent);
        }
    }
}