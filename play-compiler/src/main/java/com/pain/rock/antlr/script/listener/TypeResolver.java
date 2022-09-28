package com.pain.rock.antlr.script.listener;

import com.pain.rock.antlr.script.AnnotatedTree;
import com.pain.rock.antlr.script.ScriptBaseListener;
import com.pain.rock.antlr.script.ScriptParser;
import com.pain.rock.antlr.script.symbol.*;

/**
 * 自上而下的类型推导，也就是 I 属性的计算
 * 解析变量、类继承、函数声明
 */
public class TypeResolver extends ScriptBaseListener {
    private AnnotatedTree tree;

    // 是否把本地变量加入符号表
    private boolean enterLocalVariable = false;

    public TypeResolver(AnnotatedTree tree) {
        this.tree = tree;
    }

    public TypeResolver(AnnotatedTree tree, boolean enterLocalVariable) {
        this.tree = tree;
        this.enterLocalVariable = enterLocalVariable;
    }

    /**
     * 设置所声明的变量的类型
     */
    @Override
    public void exitVariableDeclarators(ScriptParser.VariableDeclaratorsContext ctx) {
        Scope scope = tree.scopeOfNode(ctx);

        if (scope instanceof Klass || enterLocalVariable) {
            Type type = tree.nodeToType.get(ctx.typeType());

            for (ScriptParser.VariableDeclaratorContext child : ctx.variableDeclarator()) {
                Variable variable = (Variable) tree.nodeToSymbol.get(child.variableDeclaratorId());
                variable.type = type;
            }
        }
    }

    /**
     * 类成员变量的声明加入符号表
     */
    @Override
    public void enterVariableDeclaratorId(ScriptParser.VariableDeclaratorIdContext ctx) {
        String idName = ctx.IDENTIFIER().getText();
        Scope scope = tree.scopeOfNode(ctx);

        if (scope instanceof Klass || enterLocalVariable || ctx.parent instanceof ScriptParser.FormalParameterContext) {
            Variable variable = new Variable(idName, scope, ctx);

            if (Scope.getVariable(scope, idName) != null) {
                tree.log("Variable or parameter already Declared: " + idName, ctx);
            }

            scope.addSymbol(variable);
            tree.nodeToSymbol.put(ctx, variable);
        }
    }

    @Override
    public void exitFunctionDeclaration(ScriptParser.FunctionDeclarationContext ctx) {
        Function function = (Function) tree.nodeToScope.get(ctx);

        if (ctx.typeTypeOrVoid() != null) {
            function.setReturnType(tree.nodeToType.get(ctx.typeTypeOrVoid()));
        } else {}

        Scope scope = tree.scopeOfNode(ctx);
        Function foundFunc = Scope.getFunction(scope, function.name, function.getParamTypes());

        // TODO
        if (foundFunc != null && foundFunc != function) {
            tree.log("Function or method already Declared: " + ctx.getText(), ctx);
        }
    }

    @Override
    public void exitFormalParameter(ScriptParser.FormalParameterContext ctx) {
        Type type = tree.nodeToType.get(ctx.typeType());
        Variable variable = (Variable) tree.nodeToSymbol.get(ctx.variableDeclaratorId());
        variable.type = type;
        Scope scope = tree.scopeOfNode(ctx);

        if (scope instanceof Function) {
            ((Function) scope).addParam(variable);
        }
    }

    /**
     * 设置类的父类
     */
    @Override
    public void enterClassDeclaration(ScriptParser.ClassDeclarationContext ctx) {
        Klass klass = (Klass) tree.nodeToScope.get(ctx);

        if (ctx.EXTENDS() != null) {
            String parentClassName = ctx.typeType().getText();
            Type type = tree.lookupType(parentClassName);

            if (type != null && type instanceof Klass) {
                klass.setParentKlass((Klass) type);
            } else {
                tree.log("unknown class: " + parentClassName, ctx);
            }
        }
    }

    @Override
    public void exitTypeTypeOrVoid(ScriptParser.TypeTypeOrVoidContext ctx) {
        if (ctx.VOID() != null) {
            tree.nodeToType.put(ctx, VoidType.instance());
        } else if (ctx.typeType() != null) {
            tree.nodeToType.put(ctx, (Type) tree.nodeToType.get(ctx.typeType()));
        }
    }

    @Override
    public void exitTypeType(ScriptParser.TypeTypeContext ctx) {
        // 冒泡，将下级的属性标注在本级
        if (ctx.classOrInterfaceType() != null) {
            Type type = tree.nodeToType.get(ctx.classOrInterfaceType());
            tree.nodeToType.put(ctx, type);
        } else if (ctx.functionType() != null) {
            Type type = tree.nodeToType.get(ctx.functionType());
            tree.nodeToType.put(ctx, type);
        } else if (ctx.primitiveType() != null) {
            Type type = tree.nodeToType.get(ctx.primitiveType());
            tree.nodeToType.put(ctx, type);
        }
    }

    @Override
    public void enterClassOrInterfaceType(ScriptParser.ClassOrInterfaceTypeContext ctx) {
        if (ctx.IDENTIFIER() != null) {
            Scope scope = tree.scopeOfNode(ctx);
            String idName = ctx.getText();
            Klass klass = tree.lookupKlass(scope, idName);
            tree.nodeToType.put(ctx, klass);
        }
    }

    @Override
    public void exitFunctionType(ScriptParser.FunctionTypeContext ctx) {
        DefaultFunctionType functionType = new DefaultFunctionType();
        tree.addType(functionType);
        tree.nodeToType.put(ctx, functionType);
        functionType.setReturnType(tree.nodeToType.get(ctx.typeTypeOrVoid()));

        if (ctx.typeList() != null) {
            ScriptParser.TypeListContext tlc = ctx.typeList();

            for (ScriptParser.TypeTypeContext ttc : tlc.typeType()) {
                Type type = tree.nodeToType.get(ttc);
                functionType.addParamType(type);
            }
        }
    }

    @Override
    public void exitPrimitiveType(ScriptParser.PrimitiveTypeContext ctx) {
        Type type = null;

        if (ctx.BOOLEAN() != null) {
            type = PrimitiveType.Boolean;
        } else if (ctx.INT() != null) {
            type = PrimitiveType.Integer;
        } else if (ctx.LONG() != null) {
            type = PrimitiveType.Long;
        } else if (ctx.FLOAT() != null) {
            type = PrimitiveType.Float;
        } else if (ctx.DOUBLE() != null) {
            type = PrimitiveType.Double;
        } else if (ctx.BYTE() != null) {
            type = PrimitiveType.Byte;
        } else if (ctx.SHORT() != null) {
            type = PrimitiveType.Short;
        } else if (ctx.CHAR() != null) {
            type = PrimitiveType.Char;
        } else if (ctx.STRING() != null) {
            type = PrimitiveType.String;
        }

        tree.nodeToType.put(ctx, type);
    }
}