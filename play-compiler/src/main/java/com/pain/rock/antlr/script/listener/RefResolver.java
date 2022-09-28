package com.pain.rock.antlr.script.listener;

import com.pain.rock.antlr.script.AnnotatedTree;
import com.pain.rock.antlr.script.ScriptBaseListener;
import com.pain.rock.antlr.script.ScriptParser;
import com.pain.rock.antlr.script.symbol.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.ArrayList;
import java.util.List;

/**
 * 自下而上的类型推导
 * 引用消解和类型推断
 */
public class RefResolver extends ScriptBaseListener {
    private AnnotatedTree tree;
    ParseTreeWalker typeResolverWalker = new ParseTreeWalker();
    TypeResolver typeResolver;

    private List<ScriptParser.FunctionCallContext> thisConstructors = new ArrayList<>();
    private List<ScriptParser.FunctionCallContext> superConstructors = new ArrayList<>();

    public RefResolver(AnnotatedTree tree) {
        this.tree = tree;
        typeResolver = new TypeResolver(tree, true);
    }

    /**
     * 把本地变量加到符号表。本地变量必须是边添加，边解析
     */
    @Override
    public void enterVariableDeclarators(ScriptParser.VariableDeclaratorsContext ctx) {
        Scope scope = tree.scopeOfNode(ctx);

        if (scope instanceof Block || scope instanceof Function) {
            typeResolverWalker.walk(typeResolver, ctx);
        }
    }

    @Override
    public void enterPrimary(ScriptParser.PrimaryContext ctx) {
        Scope scope = tree.scopeOfNode(ctx);
        Type type = null;

        if (ctx.IDENTIFIER() != null) {
            String idName = ctx.IDENTIFIER().getText();
            Variable variable = tree.lookupVariable(scope, idName);

            if (variable == null) {

                // 检查是否为函数，因为函数可以作为值来传递
                Function function = tree.lookupFunction(scope, idName);

                if (function != null) {
                    tree.nodeToSymbol.put(ctx, function);
                    type = function;
                } else {
                    tree.log("unknown variable or function: " + idName, ctx);
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
            Klass klass = tree.classOfNode(ctx);

            if (klass != null) {
                This variable = klass.getThis();
                tree.nodeToSymbol.put(ctx, variable);
                type = klass;
            } else {
                tree.log("keyword \"this\" can only be used inside a class", ctx);
            }
        } else if (ctx.SUPER() != null) {
            Klass klass = tree.classOfNode(ctx);

            if (klass != null) {
                Super variable = klass.getSuper();
                tree.nodeToSymbol.put(ctx, variable);
                type = klass;
            } else {
                tree.log("keyword \"super\" can only be used inside a class", ctx);
            }
        }

        tree.nodeToType.put(ctx, type);
    }

    @Override
    public void exitFunctionCall(ScriptParser.FunctionCallContext ctx) {
        if (ctx.THIS() != null) {
            thisConstructors.add(ctx);
            return;
        }

        if (ctx.SUPER() != null) {
            superConstructors.add(ctx);
            return;
        }

        if (ctx.IDENTIFIER().getText().equals("println")) {
            return;
        }

        String idName = ctx.IDENTIFIER().getText();
        List<Type> paramTypes = getParamTypes(ctx);
        boolean found = false;

        // 检查是否为点符号表达式调用，调用的是类方法
        if (ctx.parent instanceof ScriptParser.ExpressionContext) {
            ScriptParser.ExpressionContext exp = (ScriptParser.ExpressionContext) ctx.parent;

            if (exp.bop != null && exp.bop.getType() == ScriptParser.DOT) {
                Symbol symbol = tree.nodeToSymbol.get(exp.expression(0));

                if (symbol instanceof Variable && ((Variable) symbol).type instanceof Klass) {
                    Klass klass = (Klass) ((Variable) symbol).type;

                    // 查找名称和参数类型都匹配的函数。不允许名称和参数都相同，但返回值不同的情况
                    Function function = klass.getFunction(idName, paramTypes);

                    if (function != null) {
                        found = true;
                        tree.nodeToSymbol.put(ctx, function);
                        tree.nodeToType.put(ctx, function.getReturnType());
                    } else {
                        Variable variable = klass.getFunctionVariable(idName, paramTypes);

                        if (variable != null) {
                            found = true;
                            tree.nodeToSymbol.put(ctx, variable);
                            tree.nodeToType.put(ctx, ((FunctionType) variable.type).getReturnType());
                        } else {
                            tree.log("unable to find method " + idName + " in Class " + klass.name, exp);
                        }
                    }
                } else {
                    tree.log("unable to resolve a class", ctx);
                }
            }
        }

        Scope scope = tree.scopeOfNode(ctx);

        if (!found) {
            Function function = tree.lookupFunction(scope, idName, paramTypes);

            if (function != null) {
                found = true;
                tree.nodeToSymbol.put(ctx, function);
                tree.nodeToType.put(ctx, function.getReturnType());
            }
        }

        if (!found) {
            // 检查是否为类的构建函数
            Klass klass = tree.lookupKlass(scope, idName);

            if (klass != null) {
                Function function = klass.findConstructor(paramTypes);

                if (function != null) {
                    found = true;
                    tree.nodeToSymbol.put(ctx, function);
                } else if (ctx.expressionList() == null) { // 如果是与类名相同的方法，并且没有参数，就是缺省构造方法
                    found = true;
                    tree.nodeToSymbol.put(ctx, klass.getDefaultConstructor());
                } else {
                    tree.log("unknown class constructor: " + ctx.getText(), ctx);
                }

                tree.nodeToType.put(ctx, klass);
            } else { // 检查是否为函数型的变量
                Variable variable = tree.lookupFunctionVariable(scope, idName, paramTypes);

                if (variable != null && variable.type instanceof FunctionType) {
                    found = true;
                    tree.nodeToSymbol.put(ctx, variable);
                    tree.nodeToType.put(ctx, variable.type);
                } else {
                    tree.log("unknown function or function variable: " + ctx.getText(), ctx);
                }
            }
        }
    }

    @Override
    public void exitExpression(ScriptParser.ExpressionContext ctx) {
        Type type = null;

        if (ctx.bop != null && ctx.bop.getType() == ScriptParser.DOT) {
            Symbol symbol = tree.nodeToSymbol.get(ctx.expression(0));

            // 左递归，不断的把左边的节点的计算结果存到 nodeToSymbol
            if (symbol instanceof Variable && ((Variable) symbol).type instanceof Klass) {
                Klass klass = (Klass) ((Variable) symbol).type;

                if (ctx.IDENTIFIER() != null) {
                    String idName = ctx.IDENTIFIER().getText();
                    Variable variable = tree.lookupVariable(klass, idName);

                    if (variable != null) {
                        tree.nodeToSymbol.put(ctx, variable);
                        type = variable.type;
                    } else {
                        tree.log("unable to find field " + idName + " in Class " + klass.name, ctx);
                    }
                } else if (ctx.functionCall() != null) {
                    type = tree.nodeToType.get(ctx.functionCall());
                }
            } else {
                tree.log("symbol is not a qualified object：" + symbol, ctx);
            }
        } else if (ctx.primary() != null) {
            Symbol symbol = tree.nodeToSymbol.get(ctx.primary());
            tree.nodeToSymbol.put(ctx, symbol);
        }

        if (ctx.primary() != null) {
            type = tree.nodeToType.get(ctx.primary());
        } else if (ctx.functionCall() != null) {
            type = tree.nodeToType.get(ctx.functionCall());
        } else if (ctx.bop != null && ctx.expression().size() >= 2) {
            Type type1 = tree.nodeToType.get(ctx.expression(0));
            Type type2 = tree.nodeToType.get(ctx.expression(1));

            switch (ctx.bop.getType()) {
                case ScriptParser.ADD:
                    if (type1 == PrimitiveType.String || type2 == PrimitiveType.String) {
                        type = PrimitiveType.String;
                    } else if (type1 instanceof PrimitiveType && type2 instanceof PrimitiveType) {
                        type = PrimitiveType.getUpperType(type1, type2);
                    } else {
                        tree.log("operand should be PrimitiveType for additive and multiplicative operation", ctx);
                    }
                    break;
                case ScriptParser.SUB:
                case ScriptParser.MUL:
                case ScriptParser.DIV:
                    if (type1 instanceof PrimitiveType && type2 instanceof PrimitiveType) {
                        type = PrimitiveType.getUpperType(type1, type2);
                    } else {
                        tree.log("operand should be PrimitiveType for additive and multiplicative operation", ctx);
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

    @Override
    public void exitVariableInitializer(ScriptParser.VariableInitializerContext ctx) {
        if (ctx.expression() != null) {
            tree.nodeToType.put(ctx, tree.nodeToType.get(ctx.expression()));
        }
    }

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

    @Override
    public void exitProg(ScriptParser.ProgContext ctx) {
        for (ScriptParser.FunctionCallContext fcc : thisConstructors) {
            resolveThisConstructorCall(fcc);
        }

        for (ScriptParser.FunctionCallContext fcc : superConstructors) {
            resolveSuperConstructorCall(fcc);
        }
    }

    private void resolveThisConstructorCall(ScriptParser.FunctionCallContext ctx) {
        Klass klass = (Klass) tree.scopeOfNode(ctx);

        if (klass != null) {
            Function function = tree.functionOfNode(ctx);

            if (function != null && function.isConstructor()) {
                ScriptParser.FunctionDeclarationContext fdx = (ScriptParser.FunctionDeclarationContext) function.ctx;

                // 是否为构造函数中的第一句
                if (!firstStatementInFunction(fdx, ctx)) {
                    tree.log("this() must be first statement in a constructor", ctx);
                    return;
                }

                List<Type> paramTypes = getParamTypes(ctx);
                Function refer = klass.findConstructor(paramTypes);

                if (refer != null) {
                    tree.nodeToSymbol.put(ctx, refer);
                    tree.nodeToType.put(ctx, klass);
                    tree.thisFuncRef.put(function, refer);
                } else if (paramTypes.size() == 0) {
                    tree.nodeToSymbol.put(ctx, klass.defaultConstructor());
                    tree.nodeToType.put(ctx, klass);
                    tree.thisFuncRef.put(function, klass.defaultConstructor());
                } else {
                    tree.log("can not find a constructor matches this()", ctx);
                }
            } else {
                tree.log("this() should only be called inside a class constructor", ctx);
            }
        } else {
            tree.log("this() should only be called inside a class", ctx);
        }
    }

    private void resolveSuperConstructorCall(ScriptParser.FunctionCallContext ctx) {
        Klass klass = tree.classOfNode(ctx);

        if (klass != null) {
            Function function = tree.functionOfNode(ctx);

            if (function != null && function.isConstructor()) {
                Klass parentKlass = klass.getParentKlass();

                if (parentKlass != null) {
                    ScriptParser.FunctionDeclarationContext fdx = (ScriptParser.FunctionDeclarationContext) function.ctx;

                    // 是否为构造函数中的第一句
                    if (!firstStatementInFunction(fdx, ctx)) {
                        tree.log("super() must be first statement in a constructor", ctx);
                        return;
                    }

                    List<Type> paramTypes = getParamTypes(ctx);
                    Function refer = parentKlass.findConstructor(paramTypes);

                    if (refer != null) {
                        tree.nodeToSymbol.put(ctx, refer);
                        tree.nodeToType.put(ctx, klass);
                        tree.superFuncRef.put(function, refer);
                    } else if (paramTypes.size() == 0) {
                        tree.nodeToSymbol.put(ctx, parentKlass.defaultConstructor());
                        tree.nodeToType.put(ctx, klass);

                        // TODO
                        tree.superFuncRef.put(function, klass.defaultConstructor());
                    } else {
                        tree.log("can not find a constructor matches this()", ctx);
                    }
                } else {}
            } else {
                tree.log("super() should only be called inside a class constructor", ctx);
            }
        } else {
            tree.log("super() should only be called inside a class", ctx);
        }
    }

    private List<Type> getParamTypes(ScriptParser.FunctionCallContext ctx) {
        List<Type> paramTypes = new ArrayList<>();

        if (ctx.expressionList() != null) {
            for (ScriptParser.ExpressionContext exp : ctx.expressionList().expression()) {
                Type type = tree.nodeToType.get(exp);
                paramTypes.add(type);
            }
        }

        return paramTypes;
    }

    private boolean firstStatementInFunction(ScriptParser.FunctionDeclarationContext fdx, ScriptParser.FunctionCallContext ctx) {
        if (fdx.functionBody().block().blockStatements().blockStatement(0).statement() != null
                && fdx.functionBody().block().blockStatements().blockStatement(0).statement().expression() != null
                && fdx.functionBody().block().blockStatements().blockStatement(0).statement().expression().functionCall() == ctx) {
            return true;
        }

        return false;
    }
}


