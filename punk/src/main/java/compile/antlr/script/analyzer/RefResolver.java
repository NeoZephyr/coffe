package compile.antlr.script.analyzer;

import compile.antlr.script.ScriptBaseListener;
import compile.antlr.script.ScriptParser;
import compile.antlr.script.symbol.*;
import compile.antlr.script.types.FunctionType;
import compile.antlr.script.types.PrimitiveType;
import compile.antlr.script.types.Type;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.LinkedList;
import java.util.List;

/**
 * 引用消解和类型推断
 * 1. 解析所有的本地变量引用、函数调用和类成员引用
 * 2. 类型推断：从下而上推断表达式的类型
 */
public class RefResolver extends ScriptBaseListener {
    private AnnotatedTree tree = null;

    // 用于把本地变量添加到符号表，并计算类型
    ParseTreeWalker walker = new ParseTreeWalker();
    TypeResolver localVariableEnter = null;

    private List<ScriptParser.FunctionCallContext> thisConstructors = new LinkedList<>();
    private List<ScriptParser.FunctionCallContext> superConstructors = new LinkedList<>();

    public RefResolver(AnnotatedTree tree) {
        this.tree = tree;
        localVariableEnter = new TypeResolver(tree, true);
    }

    /**
     * 在结束扫描之前，把 this() 和 super() 构造函数消解掉
     */
    @Override
    public void exitProg(ScriptParser.ProgContext ctx) {
        for (ScriptParser.FunctionCallContext fcc : thisConstructors) {
            resolveThisConstructorCall(fcc);
        }

        for (ScriptParser.FunctionCallContext fcc : superConstructors) {
            resolveSuperConstructorCall(fcc);
        }
    }

    /**
     * 把本地变量加到符号表。本地变量必须是边添加，边解析，不能先添加后解析，否则会引起引用消解的错误
     */
    @Override
    public void enterVariableDeclarators(ScriptParser.VariableDeclaratorsContext ctx) {
        Scope scope = tree.enclosingScopeOfNode(ctx);

        if ((scope instanceof Block) || (scope instanceof Function)) {
            walker.walk(localVariableEnter, ctx);
        }
    }

    @Override
    public void exitPrimary(ScriptParser.PrimaryContext ctx) {
        Scope scope = tree.enclosingScopeOfNode(ctx);
        Type type = null;

        // 标识符
        if (ctx.IDENTIFIER() != null) {
            String id = ctx.IDENTIFIER().getText();
            Variable variable = tree.lookupVariable(scope, id);

            if (variable == null) {
                // 看看是不是函数，因为函数可以作为值来传递。这个时候，函数重名没法区分
                // 因为普通 Scope 中的函数是不可以重名的，所以这应该是没有问题的
                // 注意，查找 function 的时候，可能会把类的方法包含进去
                Function function = tree.lookupFunction(scope, id);

                if (function != null) {
                    tree.nodeToSymbol.put(ctx, function);
                    type = function;
                } else {
                    tree.error("unknown variable or function: " + id, ctx);
                }
            } else {
                tree.nodeToSymbol.put(ctx, variable);
                type = variable.type;
            }
        } else if (ctx.literal() != null) {
            type = tree.nodeToType.get(ctx.literal());
        } else if (ctx.expression() != null) {
            type = tree.nodeToType.get(ctx.expression());
        } else if (ctx.THIS() != null) {
            Klass klass = tree.enclosingKlassOfNode(ctx);

            if (klass != null) {
                This variable = klass.thisRef;
                tree.nodeToSymbol.put(ctx, variable);
                type = klass;
            } else {
                tree.error("keyword \"this\" can only be used inside a class", ctx);
            }
        } else if (ctx.SUPER() != null) {
            Klass klass = tree.enclosingKlassOfNode(ctx);

            if (klass != null) {
                Super variable = klass.superRef;
                tree.nodeToSymbol.put(ctx, variable);
                type = klass;
            } else {
                tree.error("keyword \"super\" can only be used inside a class", ctx);
            }
        }

        // 类型推断、冒泡
        tree.nodeToType.put(ctx, type);
    }

    @Override
    public void exitFunctionCall(ScriptParser.FunctionCallContext ctx) {
        if (ctx.THIS() != null) {
            thisConstructors.add(ctx);
            return;
        } else if (ctx.SUPER() != null) {
            superConstructors.add(ctx);
            return;
        }

        // 临时代码，支持 println
        if (ctx.IDENTIFIER().getText().equals("println")) {
            return;
        }

        String id = ctx.IDENTIFIER().getText();

        // 获得参数类型，这些类型已经在表达式中推断出来
        List<Type> paramTypes = getParamTypes(ctx);
        boolean found = false;

        // 看看是不是点符号表达式调用的，调用的是类的方法
        if (ctx.parent instanceof ScriptParser.ExpressionContext) {
            ScriptParser.ExpressionContext expr = (ScriptParser.ExpressionContext) ctx.parent;

            if ((expr.bop != null) && (expr.bop.getType() == ScriptParser.DOT)) {
                Symbol symbol = tree.nodeToSymbol.get(expr.expression(0));

                if ((symbol instanceof Variable) && (((Variable) symbol).type instanceof Klass)) {
                    Klass klass = (Klass) ((Variable) symbol).type;

                    // 查找名称和参数类型都匹配的函数。不允许名称和参数都相同，但返回值不同的情况
                    Function function = klass.getFunction(id, paramTypes);

                    if (function != null) {
                        found = true;
                        tree.nodeToSymbol.put(ctx, function);
                        tree.nodeToType.put(ctx, function.getReturnType());
                    } else {
                        Variable variable = klass.getFuncVariable(id, paramTypes);

                        if (variable != null) {
                            found = true;
                            tree.nodeToSymbol.put(ctx, variable);
                            tree.nodeToType.put(ctx, ((FunctionType) variable.type).getReturnType());
                        } else {
                            tree.error("unable to find method " + id + " in Class " + klass.name, expr);
                        }
                    }
                } else {
                    tree.error("unable to resolve a class", ctx);
                }
            }
        }

        Scope scope = tree.enclosingScopeOfNode(ctx);

        // 从当前 Scope 逐级查找函数或方法
        if (!found) {
            Function function = tree.lookupFunction(scope, id, paramTypes);

            if (function != null) {
                found = true;
                tree.nodeToSymbol.put(ctx, function);
                tree.nodeToType.put(ctx, function.returnType);
            }
        }

        if (!found) {
            // 看看是不是类的构建函数，用相同的名称查找一个 class
            Klass klass = tree.lookupKlass(scope, id);

            if (klass != null) {
                Function function = klass.findConstructor(paramTypes);

                if (function != null) {
                    found = true;
                    tree.nodeToSymbol.put(ctx, function);
                } else if (ctx.expressionList() == null) {
                    // 如果是与类名相同的方法，并且没有参数，那么就是缺省构造方法
                    found = true;
                    tree.nodeToSymbol.put(ctx, klass.defaultConstructor());
                } else {
                    tree.error("unknown class constructor: " + ctx.getText(), ctx);
                }

                // 这次函数调用是返回一个对象
                tree.nodeToType.put(ctx, klass);
            } else {
                Variable variable = tree.lookupFunctionVariable(scope, id, paramTypes);

                if ((variable != null) && (variable.type instanceof FunctionType)) {
                    found = true;
                    tree.nodeToSymbol.put(ctx, variable);
                    tree.nodeToType.put(ctx, variable.type);
                } else {
                    tree.error("unknown function or function variable: " + ctx.getText(), ctx);
                }
            }
        }
    }

    /**
     * 根据字面量来推断类型
     */
    @Override
    public void exitLiteral(ScriptParser.LiteralContext ctx) {
        if (ctx.BOOL_LITERAL() != null) {
            tree.nodeToType.put(ctx, PrimitiveType.Boolean);
        } else if (ctx.CHAR_LITERAL() != null) {
            tree.nodeToType.put(ctx, PrimitiveType.Char);
        } else if (ctx.NULL_LITERAL() != null) {
            tree.nodeToType.put(ctx, PrimitiveType.Null);
        } else if (ctx.STRING_LITERAL() != null) {
            tree.nodeToType.put(ctx, PrimitiveType.String);
        } else if (ctx.integerLiteral() != null) {
            tree.nodeToType.put(ctx, PrimitiveType.Integer);
        } else if (ctx.floatLiteral() != null) {
            tree.nodeToType.put(ctx, PrimitiveType.Float);
        }
    }

    /**
     * 对变量初始化部分也做一下类型推断
     */
    @Override
    public void exitVariableInitializer(ScriptParser.VariableInitializerContext ctx) {
        if (ctx.expression() != null) {
            tree.nodeToType.put(ctx, tree.nodeToType.get(ctx.expression()));
        }
    }

    /**
     * 消解处理点符号表达式的层层引用
     */
    @Override
    public void exitExpression(ScriptParser.ExpressionContext ctx) {
        Type type = null;

        if ((ctx.bop != null) && (ctx.bop.getType() == ScriptParser.DOT)) {
            // 这是个左递归，要不断的把左边的节点的计算结果存到 nodeToSymbol，所以要在 exitExpression 里操作
            Symbol symbol = tree.nodeToSymbol.get(ctx.expression(0));

            if ((symbol instanceof Variable) && (((Variable) symbol).type instanceof Klass)) {
                Klass klass = (Klass) ((Variable) symbol).type;

                // 引用类的属性
                if (ctx.IDENTIFIER() != null) {
                    String id = ctx.IDENTIFIER().getText();
                    Variable variable = tree.lookupVariable(klass, id);

                    if (variable != null) {
                        tree.nodeToSymbol.put(ctx, variable);
                        type = variable.type;
                    } else {
                        tree.error("unable to find field " + id + " in Class " + klass.name, ctx);
                    }
                } else if (ctx.functionCall() != null) {
                    // 引用类的方法
                    type = tree.nodeToType.get(ctx.functionCall());
                }
            } else {
                tree.error("symbol is not a qualified object：" + symbol, ctx);
            }
        } else if (ctx.primary() != null) {
            // 变量引用冒泡：如果下级是一个变量，往上冒泡传递，以便在点符号表达式中使用
            // 也包括 This 和 Super 的冒泡
            Symbol symbol = tree.nodeToSymbol.get(ctx.primary());
            tree.nodeToSymbol.put(ctx, symbol);
        }

        // 类型推断和综合
        if (ctx.primary() != null) {
            type = tree.nodeToType.get(ctx.primary());
        } else if (ctx.functionCall() != null) {
            type = tree.nodeToType.get(ctx.functionCall());
        } else if ((ctx.bop != null) && (ctx.expression().size() >= 2)) {
            Type type1 = tree.nodeToType.get(ctx.expression(0));
            Type type2 = tree.nodeToType.get(ctx.expression(1));

            switch (ctx.bop.getType()) {
                case ScriptParser.ADD:
                    if ((type1 == PrimitiveType.String) || (type2 == PrimitiveType.String)) {
                        type = PrimitiveType.String;
                    } else if ((type1 instanceof PrimitiveType) && (type2 instanceof PrimitiveType)) {
                        type = PrimitiveType.upperType(type1, type2);
                    } else {
                        tree.error("operand should be PrimitiveType for additive and multiplicative operation", ctx);
                    }
                    break;
                case ScriptParser.SUB:
                case ScriptParser.MUL:
                case ScriptParser.DIV:
                    if ((type1 instanceof PrimitiveType) && (type2 instanceof PrimitiveType)) {
                        type = PrimitiveType.upperType(type1, type2);
                    } else {
                        tree.error("operand should be PrimitiveType for additive and multiplicative operation", ctx);
                    }
                    break;
                case ScriptParser.EQUAL:
                case ScriptParser.NOTEQUAL:
                case ScriptParser.LE:
                case ScriptParser.LT:
                case ScriptParser.GE:
                case ScriptParser.GT:
                case ScriptParser.AND:
                case ScriptParser.OR:
                case ScriptParser.BANG:
                    type = PrimitiveType.Boolean;
                    break;
                case ScriptParser.ASSIGN:
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
                    type = type1;
                    break;
            }
        }

        tree.nodeToType.put(ctx, type);
    }

    private void resolveThisConstructorCall(ScriptParser.FunctionCallContext ctx) {
        Klass klass = tree.enclosingKlassOfNode(ctx);

        if (klass != null) {
            Function function = tree.enclosingFunctionOfNode(ctx);

            if ((function != null) && function.isConstructor()) {
                ScriptParser.FunctionDeclarationContext fdx = (ScriptParser.FunctionDeclarationContext) function.ctx;

                if (!firstStatementInFunction(fdx, ctx)) {
                    tree.error("this() must be first statement in a constructor", ctx);
                    return;
                }

                List<Type> paramTypes = getParamTypes(ctx);
                Function refer = klass.findConstructor(paramTypes);

                if (refer != null) {
                    tree.nodeToSymbol.put(ctx, refer);
                    tree.nodeToType.put(ctx, klass);
                    tree.thisConstructorRef.put(function, refer);
                } else if (paramTypes.isEmpty()) {
                    tree.nodeToSymbol.put(ctx, klass.defaultConstructor());
                    tree.nodeToType.put(ctx, klass);
                    tree.thisConstructorRef.put(function, klass.defaultConstructor());
                } else {
                    tree.error("can not find a constructor matches this()", ctx);
                }
            } else {
                tree.error("this() should only be called inside a class constructor", ctx);
            }
        } else {
            tree.error("this() should only be called inside a class", ctx);
        }
    }

    private void resolveSuperConstructorCall(ScriptParser.FunctionCallContext ctx) {
        Klass klass = tree.enclosingKlassOfNode(ctx);

        if (klass != null) {
            Function function = tree.enclosingFunctionOfNode(ctx);

            if ((function != null) && function.isConstructor()) {
                Klass parent = klass.getParent();

                if (parent != null) {
                    ScriptParser.FunctionDeclarationContext fdx = (ScriptParser.FunctionDeclarationContext) function.ctx;

                    if (!firstStatementInFunction(fdx, ctx)) {
                        tree.error("super() must be first statement in a constructor", ctx);
                        return;
                    }

                    List<Type> paramTypes = getParamTypes(ctx);
                    Function refer = parent.findConstructor(paramTypes);

                    if (refer != null) {
                        tree.nodeToSymbol.put(ctx, refer);
                        tree.nodeToType.put(ctx, klass);
                        tree.superConstructorRef.put(function, refer);
                    } else if (paramTypes.isEmpty()) {
                        // 缺省构造函数
                        tree.nodeToSymbol.put(ctx, parent.defaultConstructor());
                        tree.nodeToType.put(ctx, klass);
                        tree.superConstructorRef.put(function, klass.defaultConstructor());
                    } else {
                        tree.error("can not find a constructor matches this()", ctx);
                    }
                } else {} // 父类是最顶层的基类
            } else {
                tree.error("super() should only be called inside a class constructor", ctx);
            }
        } else {
            tree.error("super() should only be called inside a class", ctx);
        }
    }

    private boolean firstStatementInFunction(ScriptParser.FunctionDeclarationContext fdx, ScriptParser.FunctionCallContext ctx) {
        ScriptParser.StatementContext statement = fdx.functionBody().block().blockStatements().blockStatement(0).statement();

        return (statement.statement() != null) && (statement.expression() != null) && (statement.expression().functionCall() == ctx);
    }

    private List<Type> getParamTypes(ScriptParser.FunctionCallContext ctx) {
        List<Type> paramTypes = new LinkedList<>();

        if (ctx.expressionList() != null) {
            for (ScriptParser.ExpressionContext expr : ctx.expressionList().expression()) {
                Type type = tree.nodeToType.get(expr);
                paramTypes.add(type);
            }
        }

        return paramTypes;
    }
}