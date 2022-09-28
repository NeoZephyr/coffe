package com.pain.rock.antlr.script.listener;

import com.pain.rock.antlr.script.AnnotatedTree;
import com.pain.rock.antlr.script.ScriptBaseListener;
import com.pain.rock.antlr.script.ScriptParser;
import com.pain.rock.antlr.script.symbol.*;
import org.antlr.v4.runtime.ParserRuleContext;

public class TypeChecker extends ScriptBaseListener {

    private AnnotatedTree tree;

    public TypeChecker(AnnotatedTree tree) {
        this.tree = tree;
    }

    @Override
    public void exitVariableDeclarator(ScriptParser.VariableDeclaratorContext ctx) {
        if (ctx.variableInitializer() != null) {
            Variable variable = (Variable) tree.nodeToSymbol.get(ctx.variableDeclaratorId());
            Type type1 = variable.type;
            Type type2 = tree.nodeToType.get(ctx.variableInitializer());
            checkAssign(type1, type2, ctx, ctx.variableDeclaratorId(), ctx.variableInitializer());
        }
    }

    @Override
    public void exitExpression(ScriptParser.ExpressionContext ctx) {
        if (ctx.bop != null && ctx.expression().size() >= 2) {
            Type type1 = tree.nodeToType.get(ctx.expression(0));
            Type type2 = tree.nodeToType.get(ctx.expression(1));

            switch (ctx.bop.getType()) {
                case ScriptParser.ADD:
                    if (type1 != PrimitiveType.String && type2 != PrimitiveType.String) {
                        checkNumericOperand(type1, ctx, ctx.expression(0));
                        checkNumericOperand(type2, ctx, ctx.expression(1));
                    }
                    break;
                case ScriptParser.SUB:
                case ScriptParser.MUL:
                case ScriptParser.DIV:
                case ScriptParser.LE:
                case ScriptParser.LT:
                case ScriptParser.GE:
                case ScriptParser.GT:
                    checkNumericOperand(type1, ctx, ctx.expression(0));
                    checkNumericOperand(type2, ctx, ctx.expression(1));
                    break;
                case ScriptParser.EQUAL:
                case ScriptParser.NOTEQUAL:
                    break;
                case ScriptParser.AND:
                case ScriptParser.OR:
                    checkBooleanOperand(type1, ctx, ctx.expression(0));
                    checkBooleanOperand(type2, ctx, ctx.expression(1));
                    break;
                case ScriptParser.ASSIGN:
                    checkAssign(type1, type2, ctx, ctx.expression(0), ctx.expression(1));
                    break;
                case ScriptParser.ADD_ASSIGN:
                case ScriptParser.SUB_ASSIGN:
                case ScriptParser.MUL_ASSIGN:
                case ScriptParser.DIV_ASSIGN:
                case ScriptParser.AND_ASSIGN:
                case ScriptParser.OR_ASSIGN:
                case ScriptParser.XOR_ASSIGN:
                case ScriptParser.MOD_ASSIGN:
                case ScriptParser.LSHIFT_ASSIGN:
                case ScriptParser.RSHIFT_ASSIGN:
                case ScriptParser.URSHIFT_ASSIGN:
                    if (PrimitiveType.isNumeric(type2)) {
                        if (!checkNumericAssign(type2, type1)) {
                            tree.log("can not assign " + ctx.expression(1).getText() + " of type " + type2 + " to " + ctx.expression(0) + " of type " + type1, ctx);
                        }
                    }
                    else{
                        tree.log("operand + " + ctx.expression(1).getText() + " should be numeric。", ctx );
                    }

                    break;
            }
        }
    }

    private void checkNumericOperand(Type type, ScriptParser.ExpressionContext exp, ScriptParser.ExpressionContext operand) {
        if (!PrimitiveType.isNumeric(type)) {
            tree.log("operand for arithmetic operation should be numeric : " + operand.getText(), exp);
        }
    }

    private void checkBooleanOperand(Type type, ScriptParser.ExpressionContext exp, ScriptParser.ExpressionContext operand) {
        if (type != PrimitiveType.Boolean) {
            tree.log("operand for logical operation should be boolean : " + operand.getText(), exp);
        }
    }

    private void checkAssign(Type type1, Type type2, ParserRuleContext ctx, ParserRuleContext operand1, ParserRuleContext operand2) {
        if (PrimitiveType.isNumeric(type2)) {
            if (!checkNumericAssign(type2, type1)) {
                tree.log("can not assign " + operand2.getText() + " of type " + type2 + " to " + operand1.getText() + " of type " + type1, ctx);
            }
        } else if (type2 instanceof Klass) {

        } else if (type2 instanceof Function) {

        }
    }

    private boolean checkNumericAssign(Type from, Type to) {
        if (to == PrimitiveType.Double) {
            return PrimitiveType.isNumeric(from);
        } else if (to == PrimitiveType.Float) {
            return (from == PrimitiveType.Byte ||
                    from == PrimitiveType.Short ||
                    from == PrimitiveType.Integer ||
                    from == PrimitiveType.Long ||
                    from == PrimitiveType.Float);
        } else if (to == PrimitiveType.Long) {
            return (from == PrimitiveType.Byte ||
                    from == PrimitiveType.Short ||
                    from == PrimitiveType.Integer ||
                    from == PrimitiveType.Long);
        } else if (to == PrimitiveType.Integer) {
            return (from == PrimitiveType.Byte ||
                    from == PrimitiveType.Short ||
                    from == PrimitiveType.Integer);
        } else if (to == PrimitiveType.Short) {
            return (from == PrimitiveType.Byte ||
                    from == PrimitiveType.Short);
        } else if (to == PrimitiveType.Byte) {
            return from == PrimitiveType.Byte;
        }

        return false;
    }
}