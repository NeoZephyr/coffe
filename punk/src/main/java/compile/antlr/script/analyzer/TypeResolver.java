package compile.antlr.script.analyzer;

import compile.antlr.script.ScriptBaseListener;
import compile.antlr.script.ScriptParser;
import compile.antlr.script.symbol.Function;
import compile.antlr.script.symbol.Klass;
import compile.antlr.script.symbol.Scope;
import compile.antlr.script.symbol.Variable;
import compile.antlr.script.types.DefaultFunctionType;
import compile.antlr.script.types.PrimitiveType;
import compile.antlr.script.types.Type;
import compile.antlr.script.types.VoidType;

/**
 * 解析变量、类继承、函数声明
 */
public class TypeResolver extends ScriptBaseListener {
    private AnnotatedTree tree = null;

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
        Scope scope = tree.enclosingScopeOfNode(ctx);

        if ((scope instanceof Klass) || enterLocalVariable) {
            Type type = tree.nodeToType.get(ctx.type());

            for (ScriptParser.VariableDeclaratorContext child : ctx.variableDeclarator()) {
                Variable variable = (Variable) tree.nodeToSymbol.get(child.variableDeclaratorId());
                variable.type = type;
            }
        }
    }

    /**
     * 把类成员变量的声明加入符号表
     */
    @Override
    public void enterVariableDeclaratorId(ScriptParser.VariableDeclaratorIdContext ctx) {
        String id = ctx.IDENTIFIER().getText();
        Scope scope = tree.enclosingScopeOfNode(ctx);

        // 第一步只把类的成员变量入符号表。在变量消解时，再把本地变量加入符号表，一边 Enter，一边消解

        if ((scope instanceof Klass) || enterLocalVariable || (ctx.parent instanceof ScriptParser.FormalParameterContext)) {
            Variable variable = new Variable(id, scope, ctx);

            if (Scope.getVariable(scope, id) != null) {
                tree.error("Variable or parameter already Declared: " + id, ctx);
            }

            scope.addSymbol(variable);
            tree.nodeToSymbol.put(ctx, variable);
        }
    }

    /**
     * 设置函数的返回值类型
     */
    @Override
    public void exitFunctionDeclaration(ScriptParser.FunctionDeclarationContext ctx) {
        Function function = (Function) tree.nodeToScope.get(ctx);

        if (ctx.typeOrVoid() != null) {
            function.returnType = tree.nodeToType.get(ctx.typeOrVoid());
        } else {
            // 如果是类的构建函数，返回值应该是一个类 ?
        }

        Scope scope = tree.enclosingScopeOfNode(ctx);
        Function exist = Scope.getFunction(scope, function.name, function.getParamTypes());

        if ((exist != null) && (exist != function)) {
            tree.error("Function or method already Declared: " + ctx.getText(), ctx);
        }
    }

    /**
     * 设置函数的参数的类型，这些参数已经在 enterVariableDeclaratorId 中作为变量声明了，现在设置它们的类型
     */
    @Override
    public void exitFormalParameter(ScriptParser.FormalParameterContext ctx) {
        Type type = tree.nodeToType.get(ctx.type());
        Variable variable = (Variable) tree.nodeToSymbol.get(ctx.variableDeclaratorId());
        variable.type = type;

        Scope scope = tree.enclosingScopeOfNode(ctx);

        if (scope instanceof Function) {
            ((Function) scope).params.add(variable);
        }
    }

    /**
     * 设置类的父类
     */
    @Override
    public void enterClassDeclaration(ScriptParser.ClassDeclarationContext ctx) {
        Klass klass = (Klass) tree.nodeToScope.get(ctx);

        if (ctx.EXTENDS() != null) {
            String parentKlassName = ctx.type().getText();
            Type type = tree.lookupType(parentKlassName);

            if (type instanceof Klass) {
                klass.setParent((Klass) type);
            } else {
                tree.error("unknown class: " + parentKlassName, ctx);
            }
        }
    }

    @Override
    public void exitTypeOrVoid(ScriptParser.TypeOrVoidContext ctx) {
        if (ctx.VOID() != null) {
            tree.nodeToType.put(ctx, VoidType.instance());
        } else if (ctx.type() != null) {
            tree.nodeToType.put(ctx, tree.nodeToType.get(ctx.type()));
        }
    }

    @Override
    public void exitType(ScriptParser.TypeContext ctx) {
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
            Scope scope = tree.enclosingScopeOfNode(ctx);
            String id = ctx.getText();
            Klass klass = tree.lookupKlass(scope, id);
            tree.nodeToType.put(ctx, klass);
        }
    }

    @Override
    public void exitFunctionType(ScriptParser.FunctionTypeContext ctx) {
        DefaultFunctionType functionType = new DefaultFunctionType();
        functionType.returnType = tree.nodeToType.get(ctx.typeOrVoid());
        tree.types.add(functionType);
        tree.nodeToType.put(ctx, functionType);

        if (ctx.typeList() != null) {
            ScriptParser.TypeListContext tcl = ctx.typeList();

            for (ScriptParser.TypeContext tc : tcl.type()) {
                Type type = tree.nodeToType.get(tc);
                functionType.paramTypes.add(type);
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