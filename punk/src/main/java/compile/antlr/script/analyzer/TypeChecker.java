package compile.antlr.script.analyzer;

import compile.antlr.script.ScriptBaseListener;
import compile.antlr.script.ScriptParser;
import compile.antlr.script.symbol.Function;
import compile.antlr.script.symbol.Klass;
import compile.antlr.script.symbol.Variable;
import compile.antlr.script.types.PrimitiveType;
import compile.antlr.script.types.Type;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * 类型检查
 * 1. 赋值表达式
 * 2. 变量初始化
 * 3. 表达式里的一些运算，比如加减乘除，是否类型匹配
 * 4. 返回值的类型
 */
public class TypeChecker extends ScriptBaseListener {
    private AnnotatedTree tree = null;

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
        if ((ctx.bop != null) && (ctx.expression().size() >= 2)) {
            Type type1 = tree.nodeToType.get(ctx.expression(0));
            Type type2 = tree.nodeToType.get(ctx.expression(1));

            switch (ctx.bop.getType()) {
                case ScriptParser.ADD:
                    // 字符串能够跟任何对象做 + 运算
                    if ((type1 != PrimitiveType.String) && (type2 != PrimitiveType.String)) {
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
                            tree.error("can not assign " + ctx.expression(1).getText() + " of type " + type2 + " to " + ctx.expression(0) + " of type " + type1, ctx);
                        }
                    } else {
                        tree.error("operand + " + ctx.expression(1).getText() + " should be numeric", ctx );
                    }
                    break;
            }
        }
    }

    private void checkNumericOperand(Type type,
                                     ScriptParser.ExpressionContext expr,
                                     ScriptParser.ExpressionContext operand) {
        if (!(PrimitiveType.isNumeric(type))) {
            tree.error("operand for arithmetic operation should be numeric : " + operand.getText(), expr);
        }
    }

    private void checkBooleanOperand(Type type, ScriptParser.ExpressionContext expr, ScriptParser.ExpressionContext operand) {
        if (!(type == PrimitiveType.Boolean)) {
            tree.error("operand for logical operation should be boolean : " + operand.getText(), expr);
        }
    }

    private void checkAssign(Type type1, Type type2, ParserRuleContext ctx, ParserRuleContext operand1, ParserRuleContext operand2) {
        if (PrimitiveType.isNumeric(type2)) {
            if (!checkNumericAssign(type2, type1)) {
                tree.error("can not assign " + operand2.getText() + " of type " + type2 + " to " + operand1.getText() + " of type " + type1, ctx);
            }
        } else if (type2 instanceof Klass) {
            // TODO 检查类的兼容性
        } else if (type2 instanceof Function) {
            // TODO 检查函数的兼容性
        }
    }

    private boolean checkNumericAssign(Type from, Type to) {
        boolean assign = false;

        if (to == PrimitiveType.Double) {
            assign = (PrimitiveType.isNumeric(from));
        } else if (to == PrimitiveType.Float) {
            assign = ((from == PrimitiveType.Byte) ||
                    (from == PrimitiveType.Short) ||
                    (from == PrimitiveType.Integer) ||
                    (from == PrimitiveType.Long) ||
                    (from == PrimitiveType.Float));
        } else if (to == PrimitiveType.Long) {
            assign = ((from == PrimitiveType.Byte) ||
                    (from == PrimitiveType.Short) ||
                    (from == PrimitiveType.Integer) ||
                    (from == PrimitiveType.Long));
        } else if (to == PrimitiveType.Integer) {
            assign = ((from == PrimitiveType.Byte) ||
                    (from == PrimitiveType.Short) ||
                    (from == PrimitiveType.Integer));
        } else if (to == PrimitiveType.Short) {
            assign = ((from == PrimitiveType.Byte) ||
                    (from == PrimitiveType.Short));
        } else if (to == PrimitiveType.Byte) {
            assign = (from == PrimitiveType.Byte);
        }

        return assign;
    }
}