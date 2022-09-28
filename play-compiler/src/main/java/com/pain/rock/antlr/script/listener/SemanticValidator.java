package com.pain.rock.antlr.script.listener;

import com.pain.rock.antlr.script.AnnotatedTree;
import com.pain.rock.antlr.script.ScriptBaseListener;
import com.pain.rock.antlr.script.ScriptParser;
import com.pain.rock.antlr.script.symbol.Function;
import com.pain.rock.antlr.script.symbol.Type;
import com.pain.rock.antlr.script.symbol.VoidType;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

public class SemanticValidator extends ScriptBaseListener {

    private AnnotatedTree tree;

    public SemanticValidator(AnnotatedTree tree) {
        this.tree = tree;
    }

    @Override
    public void exitPrimary(ScriptParser.PrimaryContext ctx) {
    }

    @Override
    public void exitFunctionCall(ScriptParser.FunctionCallContext ctx) {
    }

    @Override
    public void exitExpression(ScriptParser.ExpressionContext ctx) {
    }

    @Override
    public void exitClassDeclaration(ScriptParser.ClassDeclarationContext ctx) {
        // 类的声明不能在函数里
        if (tree.functionOfNode(ctx) != null) {
            tree.log("can not declare class inside function", ctx);
        }
    }

    @Override
    public void exitFunctionDeclaration(ScriptParser.FunctionDeclarationContext ctx) {
        if (ctx.typeTypeOrVoid() != null) {
            if (!hasReturnStatement(ctx)) {
                Type returnType = tree.nodeToType.get(ctx.typeTypeOrVoid());

                if (returnType != VoidType.instance()) {
                    tree.log("return statment expected in function", ctx);
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

    @Override
    public void exitVariableInitializer(ScriptParser.VariableInitializerContext ctx) {
        super.exitVariableInitializer(ctx);
    }

    @Override
    public void exitLiteral(ScriptParser.LiteralContext ctx) {
    }

    @Override
    public void exitStatement(ScriptParser.StatementContext ctx) {
        if (ctx.RETURN() != null) {
            Function function = tree.functionOfNode(ctx);

            if (function == null) {
                tree.log("return statement not in function body", ctx);
            } else if (function.isConstructor() && ctx.expression() != null) {
                tree.log("can not return a value from constructor", ctx);
            }
        } else if (ctx.BREAK() != null) {
            if (!checkBreak(ctx)) {
                tree.log("break statement not in loop or switch statements", ctx);
            }
        }
    }

    private boolean hasReturnStatement(ParseTree ctx) {
        boolean flag = false;

        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);

            if (child instanceof ScriptParser.StatementContext &&
                    ((ScriptParser.StatementContext) child).RETURN() != null) {
                flag = true;
                break;
            } else if (!(child instanceof ScriptParser.FunctionDeclarationContext || child instanceof ScriptParser.ClassDeclarationContext)) {
                flag = hasReturnStatement(child);

                if (flag) {
                    break;
                }
            }
        }

        return flag;
    }

    private boolean checkBreak(RuleContext ctx) {
        if (ctx.parent instanceof ScriptParser.StatementContext &&
                (((ScriptParser.StatementContext) ctx.parent).FOR() != null ||
                        ((ScriptParser.StatementContext) ctx.parent).WHILE() != null) ||
                ctx.parent instanceof ScriptParser.SwitchBlockStatementGroupContext) {
            return true;
        } else if (ctx.parent == null || ctx.parent instanceof ScriptParser.FunctionDeclarationContext) {
            return false;
        } else {
            return checkBreak(ctx.parent);
        }
    }
}