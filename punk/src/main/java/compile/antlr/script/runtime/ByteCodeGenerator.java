package compile.antlr.script.runtime;

import compile.antlr.script.ScriptBaseVisitor;
import compile.antlr.script.ScriptParser;
import compile.antlr.script.analyzer.AnnotatedTree;
import compile.antlr.script.symbol.Function;
import compile.antlr.script.symbol.Symbol;
import compile.antlr.script.symbol.Variable;
import compile.antlr.script.types.Type;
import compile.antlr.script.types.VoidType;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

public class ByteCodeGenerator extends ScriptBaseVisitor<Object> implements Opcodes {
    private AnnotatedTree tree = null;

    ClassWriter writer = null;

    MethodVisitor visitor = null;

    // 下一个本地变量的下标，要把方法的参数也算进去
    int localVarIdx = 0;

    // 变量名与下标的映射表
    Map<String, Integer> varNameToIdx = new HashMap<>();

    boolean returnGenerated = false;

    int instanceIdx = 0;

    public ByteCodeGenerator(AnnotatedTree tree) {
        this.tree = tree;
    }

    public byte[] generate() {
        writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        visitProg((ScriptParser.ProgContext) tree.ast);

        for (Type type : tree.types) {
            if (type instanceof Function) {
                Function function = (Function) type;
                ScriptParser.FunctionDeclarationContext ctx = (ScriptParser.FunctionDeclarationContext) function.ctx;
                genMethod(ctx);
            }
        }

        return writer.toByteArray();
    }

    @Override
    public Object visitProg(ScriptParser.ProgContext ctx) {
        // 把全局的变量和函数封装到一个缺省的类中
        writer.visit(
                Opcodes.V1_8,
                ACC_PUBLIC + ACC_SUPER,
                "ScriptClass",
                null,
                "java/lang/Object",
                null
        );

        // 缺省的构造函数
        genDefaultConstructor(writer);
        visitor = writer.visitMethod(
                ACC_PUBLIC + ACC_STATIC,
                "main",
                "([Ljava/lang/String;)V",
                null,
                null
        );
        Label l0 = new Label();
        visitor.visitLabel(l0);
        visitor.visitParameter("args", ACC_PUBLIC);
        visitor.visitTypeInsn(NEW, "ScriptClass");
        visitor.visitInsn(DUP);
        visitor.visitMethodInsn(INVOKESPECIAL, "ScriptClass", "<INIT>", "()v", false);
        visitor.visitVarInsn(ASTORE, 1);

        localVarIdx = 1;
        instanceIdx = 1;

        visitBlockStatements(ctx.blockStatements());
        visitor.visitInsn(RETURN);

        Label l1 = new Label();
        visitor.visitLabel(l1);

        for (String varName : varNameToIdx.keySet()) {
            int varIdx = varNameToIdx.get(varName);
            visitor.visitLocalVariable(varName, "I", null, l0, l1, varIdx);
        }

        // 设置操作数栈最大的帧数，以及最大的本地变量数
        visitor.visitMaxs(3, 1);
        visitor.visitEnd();

        return null;
    }

    @Override
    public Object visitVariableDeclarator(ScriptParser.VariableDeclaratorContext ctx) {
        String varName = ctx.variableDeclaratorId().getText();
        localVarIdx++;
        varNameToIdx.put(varName, localVarIdx);

        if (ctx.variableInitializer() != null) {
            visitVariableInitializer(ctx.variableInitializer());
        }

        return null;
    }

    @Override
    public Object visitVariableInitializer(ScriptParser.VariableInitializerContext ctx) {
        // 把初始化值压到栈里
        if (ctx.expression() != null) {
            visitExpression(ctx.expression());
        }

        // 把栈里的数据存到变量里去
        visitor.visitVarInsn(ISTORE, localVarIdx);
        return null;
    }

    @Override
    public Object visitStatement(ScriptParser.StatementContext ctx) {
        if (ctx.statementExpression != null) {
            visitExpression(ctx.statementExpression);
        } else if (ctx.RETURN() != null) {
            if (ctx.expression() != null) {
                visitExpression(ctx.expression());

                // 返回整数
                visitor.visitInsn(IRETURN);
            } else {
                visitor.visitInsn(RETURN);
            }

            returnGenerated = true;
        }

        return null;
    }

    @Override
    public Object visitExpression(ScriptParser.ExpressionContext ctx) {
        String address = "";

        // 赋值
        if ((ctx.bop != null) && (ctx.bop.getType() == ScriptParser.ASSIGN)) {
            // 左侧必须是一个左值，现在只考虑变量名
            if ((ctx.expression(0).primary() != null) && (ctx.expression(0).primary().IDENTIFIER() != null)) {
                String varName = ctx.expression(0).getText();
                int varIdx = varNameToIdx.get(varName);

                // 右边必须是一个右值
                visitExpression(ctx.expression(1));

                // 设置变量的值
                visitor.visitVarInsn(ISTORE, varIdx);
            }
        } else if ((ctx.bop != null) && (ctx.expression().size() >= 2)) {
            visitExpression(ctx.expression(0));
            visitExpression(ctx.expression(1));

            switch (ctx.bop.getType()) {
                case ScriptParser.ADD:
                    visitor.visitInsn(IADD);
                    break;
                case ScriptParser.SUB:
                    visitor.visitInsn(ISUB);
                    break;
                case ScriptParser.MUL:
                    visitor.visitInsn(IMUL);
                    break;
                case ScriptParser.DIV:
                    visitor.visitInsn(IDIV);
                    break;
            }
        } else if (ctx.primary() != null) {
            visitPrimary(ctx.primary());
        } else if (ctx.functionCall() != null) {
            visitFunctionCall(ctx.functionCall());
        }

        return address;
    }

    @Override
    public Object visitPrimary(ScriptParser.PrimaryContext ctx) {
        String result = "";

        if (ctx.literal() != null) {
            visitLiteral(ctx.literal());
        } else if (ctx.IDENTIFIER() != null) {
            Symbol symbol = tree.nodeToSymbol.get(ctx);

            if (symbol instanceof Variable) {
                Variable variable = (Variable) symbol;
                int varIdx = varNameToIdx.get(variable.name);
                visitor.visitVarInsn(ILOAD, varIdx);
            }
        }

        return result;
    }

    @Override
    public Object visitLiteral(ScriptParser.LiteralContext ctx) {
        String result = "";

        if (ctx.integerLiteral() != null) {
            visitIntegerLiteral(ctx.integerLiteral());
        } else if (ctx.STRING_LITERAL() != null) {

        }

        return result;
    }

    @Override
    public Object visitIntegerLiteral(ScriptParser.IntegerLiteralContext ctx) {
        int value = 0;

        if (ctx.DECIMAL_LITERAL() != null) {
            value = Integer.parseInt(ctx.DECIMAL_LITERAL().getText());
        }

        // 0-5 之间的数字，直接用快捷指令
        if ((value >= 0) && (value <= 5)) {
            switch (value) {
                case 0:
                    visitor.visitInsn(ICONST_0);
                    break;
                case 1:
                    visitor.visitInsn(ICONST_1);
                    break;
                case 2:
                    visitor.visitInsn(ICONST_2);
                    break;
                case 3:
                    visitor.visitInsn(ICONST_3);
                    break;
                case 4:
                    visitor.visitInsn(ICONST_4);
                    break;
                case 5:
                    visitor.visitInsn(ICONST_5);
                    break;
            }
        } else if ((value >= -128) && (value < 128)) {
            // 如果是 8 位整数，用 bipush 指令，直接放在后面的一个字节的操作数里就行了
            visitor.visitIntInsn(BIPUSH, value);
        } else if ((value >= -32768) && (value < 32768)) {
            visitor.visitIntInsn(SIPUSH, value);
        } else {
            // 大于 16 位的，采用 ldc 指令，从常量池中去取
            visitor.visitLdcInsn(value);
        }

        return null;
    }

    @Override
    public Object visitFunctionCall(ScriptParser.FunctionCallContext ctx) {
        Function function = null;
        Symbol symbol = tree.nodeToSymbol.get(ctx);

        if (symbol instanceof Function) {
            function = (Function) symbol;
        } else {
            if (ctx.IDENTIFIER().getText().equals("println")) {
                genPrintln(ctx.expressionList().expression(0));
                return null;
            } else {
                tree.error("unable to find function " + ctx.IDENTIFIER().getText(), ctx);
            }
        }

        // 把对象示例放入栈，作为第一个参数
        visitor.visitVarInsn(ALOAD, instanceIdx);

        if (ctx.expressionList() != null) {
            visitExpressionList(ctx.expressionList());
        }

        // 调用方法
        visitor.visitMethodInsn(
                INVOKEVIRTUAL,
                "ScriptClass",
                function.name,
                genFunctionDescriptor(function),
                false);
        return null;
    }

    @Override
    public Object visitFunctionDeclaration(ScriptParser.FunctionDeclarationContext ctx) {
        return null;
    }

    // 根据函数生成方法。是在 main 函数生成完毕以后调用
    private void genMethod(ScriptParser.FunctionDeclarationContext ctx) {
        // 重置中间变量
        localVarIdx = 0;
        varNameToIdx = new HashMap<>();
        instanceIdx = 0;

        Function function = (Function) tree.nodeToScope.get(ctx);
        visitor = writer.visitMethod(
                ACC_PUBLIC,
                function.name,
                genFunctionDescriptor(function),
                null,
                null);
        Label l0 = new Label();
        visitor.visitLabel(l0);

        // 添加参数
        for (Variable param : function.params) {
            visitor.visitParameter(param.name, ACC_PUBLIC);

            // 参数也存在栈桢中的本地变量列表中
            localVarIdx++;
            varNameToIdx.put(param.name, localVarIdx);
        }

        int lastParamIdx = localVarIdx;

        // 生成中间的代码
        visitFunctionBody(ctx.functionBody());

        if (!returnGenerated) {
            visitor.visitInsn(RETURN);
        }

        Label l1 = new Label();
        visitor.visitLabel(l1);

        // 设置本地变量，这一定要放在最后
        for (String varName : varNameToIdx.keySet()) {
            int varIdx = varNameToIdx.get(varName);

            if (varIdx > lastParamIdx) {
                visitor.visitLocalVariable(varName, "I", null, l0, l1, varIdx);
            }
        }

        // 设置操作数栈最大的帧数，以及最大的本地变量数
        visitor.visitMaxs(3, 1);

        // 结束方法
        visitor.visitEnd();
    }

    /**
     * 创建缺省构造方法
     */
    private static void genDefaultConstructor(ClassWriter writer) {
        MethodVisitor constructor = writer.visitMethod(
                ACC_PUBLIC,
                "<init>",
                "()V",
                null,
                null);
        constructor.visitCode();
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(
                INVOKESPECIAL,
                "java/lang/Object",
                "<init>",
                "()V",
                false);
        constructor.visitInsn(RETURN);
        constructor.visitMaxs(1, 1);
        constructor.visitEnd();
    }

    private void genPrintln(ScriptParser.ExpressionContext ctx) {
        visitor.visitFieldInsn(
                GETSTATIC,
                "java/lang/System",
                "out",
                "Ljava/io/PrintStream;");

        // 计算参数
        visitExpression(ctx);
        visitor.visitMethodInsn(
                INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(I)V",
                false);
    }

    /**
     * 形成函数的描述符。参数只支持 int 型的，返回值只支持 void 和 int
     */
    private String genFunctionDescriptor(Function function) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');

        for (int i = 0; i < function.params.size(); i++) {
            sb.append('I');
        }

        sb.append(')');

        if (function.getReturnType() instanceof VoidType) {
            sb.append('V');
        } else {
            sb.append('I');
        }

        return sb.toString();
    }
}