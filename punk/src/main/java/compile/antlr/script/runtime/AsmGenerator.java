package compile.antlr.script.runtime;

import compile.antlr.script.ScriptBaseVisitor;
import compile.antlr.script.ScriptParser;
import compile.antlr.script.analyzer.AnnotatedTree;
import compile.antlr.script.symbol.Function;
import compile.antlr.script.symbol.Symbol;
import compile.antlr.script.symbol.Variable;
import compile.antlr.script.types.Type;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 1. 目前仅针对 macos，64 位
 * 2. 仅支持整型，字符串仅支持字面量
 * 3. 运算仅支持加减法
 */
public class AsmGenerator extends ScriptBaseVisitor<String> {
    private AnnotatedTree tree = null;

    // rsp 的偏移量，也就是栈桢的大小
    private int rspOffset = 0;

    // 计算过程中的临时变量存放的位置
    // key: AST 的节点
    // value: 该节点存储的地址，可以是寄存器或栈
    private Map<ParserRuleContext, String> tmpVars = new HashMap<>();

    // 本地变量存储的地址。目前只在栈中分配，不优化到寄存器中
    // key: 变量
    // value: 该节点存储的地址，在栈中
    private Map<Variable, String> localVars = new HashMap<>();

    private String[] registersl = { "%eax", "%ebx", "%r10d", "%r11d", "%r12d", "%r13d", "%r14d", "%r15d" };
    private String[] paramRegisterl = { "%edi", "%esi", "%edx", "%ecx", "%r8d", "%r9d" };
    private String[] paramRegisterq = { "%rdi", "%rsi", "%rdx", "%rcx", "%r8", "%r9" };

    // 扫描某个例程时生成的代码的缓存区
    private StringBuffer bodyAsm = new StringBuffer();

    // 字符串字面量
    private List<String> stringLiterals = new LinkedList<String>();

    public AsmGenerator(AnnotatedTree tree) {
        this.tree = tree;
    }

    public String generate() {
        StringBuilder sb = new StringBuilder();

        // 1. 代码段的头
        sb.append("\t.section	__TEXT,__text,regular,pure_instructions\n");

        // 2. 生成函数的代码
        for (Type type : tree.types) {
            if (type instanceof Function) {
                Function function = (Function) type;
                ScriptParser.FunctionDeclarationContext fdc = (ScriptParser.FunctionDeclarationContext) function.ctx;
                visitFunctionDeclaration(fdc);
                generateProcedure(function.name, sb);
            }
        }

        // 3. 对主程序生成 main 函数
        visitProg((ScriptParser.ProgContext) tree.ast);
        generateProcedure("main", sb);

        // 4. 文本字面量
        sb.append("\n# 字符串字面量\n");
        sb.append("\t.section	__TEXT,__cstring,cstring_literals\n");

        for(int i = 0; i< stringLiterals.size(); i++){
            sb.append("L.str." + i + ":\n");
            sb.append("\t.asciz\t\"").append(stringLiterals.get(i)).append("\"\n");
        }

        // 5. 重置全局的一些临时变量
        stringLiterals.clear();

        return sb.toString();
    }

    @Override
    public String visitProg(ScriptParser.ProgContext ctx) {
        return visitBlockStatements(ctx.blockStatements());
    }

    @Override
    public String visitBlockStatements(ScriptParser.BlockStatementsContext ctx) {
        StringBuilder sb = new StringBuilder();

        for (ScriptParser.BlockStatementContext child : ctx.blockStatement()) {
            sb.append(visitBlockStatement(child));
        }
        return sb.toString();
    }

    @Override
    public String visitBlockStatement(ScriptParser.BlockStatementContext ctx) {
        StringBuilder sb = new StringBuilder();

        if (ctx.variableDeclarators() != null) {
            sb.append(visitVariableDeclarators(ctx.variableDeclarators()));
        } else if (ctx.statement() != null) {
            sb.append(visitStatement(ctx.statement()));
        }
        return sb.toString();
    }

    @Override
    public String visitVariableDeclarators(ScriptParser.VariableDeclaratorsContext ctx) {
        StringBuilder sb = new StringBuilder();

        for (ScriptParser.VariableDeclaratorContext child : ctx.variableDeclarator()) {
            sb.append(visitVariableDeclarator(child));
        }
        return sb.toString();
    }

    @Override
    public String visitVariableDeclarator(ScriptParser.VariableDeclaratorContext ctx) {
        String varAddress = visitVariableDeclaratorId(ctx.variableDeclaratorId());

        if (ctx.variableInitializer() != null) {
            String value = visitVariableInitializer(ctx.variableInitializer());
            bodyAsm.append("\tmovl\t").append(value).append(", ").append(varAddress).append("\n");
        }
        return varAddress;
    }

    @Override
    public String visitVariableDeclaratorId(ScriptParser.VariableDeclaratorIdContext ctx) {
        // 本地整型变量占 4 字节
        rspOffset += 4;
        String result = "-" + rspOffset + "(%rbp)";
        Symbol symbol = tree.nodeToSymbol.get(ctx);
        localVars.put((Variable) symbol, result);
        return result;
    }

    @Override
    public String visitVariableInitializer(ScriptParser.VariableInitializerContext ctx) {
        String result = "";

        if (ctx.expression() != null) {
            result = visitExpression(ctx.expression());
        }
        return result;
    }

    @Override
    public String visitExpression(ScriptParser.ExpressionContext ctx) {
        String address = "";

        // 二元运算
        if (ctx.bop != null && ctx.expression().size() >= 2) {
            String left = visitExpression(ctx.expression(0));
            String right = visitExpression(ctx.expression(1));
            switch (ctx.bop.getType()) {
                case ScriptParser.ADD:
                    // 为加法运算申请一个临时的存储位置，可以是寄存器和栈
                    address = allocForExpression(ctx);
                    if (!address.equals(left)){
                        bodyAsm.append("\tmovl\t").append(left).append(", ").append(address).append("\n");
                    }
                    bodyAsm.append("\taddl\t").append(right).append(", ").append(address).append("\n");
                    break;
                case ScriptParser.SUB:
                    address = allocForExpression(ctx);
                    bodyAsm.append("\tmovl\t").append(left).append(", ").append(address).append("\n");
                    bodyAsm.append("\tsubl\t").append(right).append(", ").append(address).append("\n");
                    break;
                case ScriptParser.ASSIGN:
                    bodyAsm.append("\tmovl\t").append(right).append(", ").append(left).append("\n");
                    break;
            }
        } else if (ctx.primary() != null) {
            address = visitPrimary(ctx.primary());
        } else if (ctx.functionCall() != null) {
            address = visitFunctionCall(ctx.functionCall());
        }
        return address;
    }

    @Override
    public String visitPrimary(ScriptParser.PrimaryContext ctx) {
        String result = "";

        if (ctx.literal() != null) {
            result = visitLiteral(ctx.literal());
        } else if (ctx.IDENTIFIER() != null) {
            Symbol symbol = tree.nodeToSymbol.get(ctx);
            if (symbol instanceof Variable) {
                // TODO: 本地变量地址，暂时不支持上一级Scope的变量
                result = localVars.get((Variable) symbol);
            }
        }
        return result;
    }

    @Override
    public String visitLiteral(ScriptParser.LiteralContext ctx) {
        String result = "";

        if (ctx.integerLiteral() != null) {
            result = visitIntegerLiteral(ctx.integerLiteral());
        } else if (ctx.STRING_LITERAL() != null) {
            String withQuotationMark = ctx.STRING_LITERAL().getText();
            String withoutQuotationMark = withQuotationMark.substring(1, withQuotationMark.length() - 1);
            result = getStringLiteralAddress(withoutQuotationMark);
        }
        return result;
    }

    @Override
    public String visitIntegerLiteral(ScriptParser.IntegerLiteralContext ctx) {
        String result = "";

        if (ctx.DECIMAL_LITERAL() != null) {
            result = "$" + ctx.DECIMAL_LITERAL().getText();
        }
        return result;
    }

    @Override
    public String visitStatement(ScriptParser.StatementContext ctx) {
        String value = "";

        if (ctx.statementExpression != null) {
            value = visitExpression(ctx.statementExpression);
        } else if (ctx.RETURN() != null) {
            if (ctx.expression() != null) {
                value = visitExpression(ctx.expression());

                // 在 %eax 中设置返回值
                bodyAsm.append("\n\t# 返回值\n");

                if (value.equals("%eax")){
                    bodyAsm.append("\t# 返回值在之前的计算中,已经存入%eax\n");
                } else {
                    bodyAsm.append("\tmovl\t" + value + ", %eax\n");
                }
            }
        }
        return value;
    }

    @Override
    public String visitFunctionCall(ScriptParser.FunctionCallContext ctx) {
        // 缺省获得返回值的地方
        String address = "%eax";
        String functionName = null;
        Symbol symbol = tree.nodeToSymbol.get(ctx);

        if (symbol instanceof Function) {
            Function function = (Function) symbol;
            functionName = function.name;
        } else {
            // TODO 临时代码，用于打印输出
            if (ctx.IDENTIFIER().getText().equals("println")) {
                functionName = "printf";
            } else {
                tree.error("unable to find function " + ctx.IDENTIFIER().getText(), ctx);
            }
        }

        // 设置参数
        if (ctx.expressionList() != null) {
            int paramOffset = 0;
            int numParams = ctx.expressionList().expression().size();

            // 1. 先计算所有参数的值，这个时候可能会引起栈的变化，用来存放临时变量
            int oldOffset = rspOffset;
            List<String> values = new LinkedList<>();

            for (int i = 0; i < numParams; i++) {
                values.add(visitExpression(ctx.expressionList().expression(i)));
            }

            int offset1 = rspOffset - oldOffset;

            // 2. 扩展栈
            if (numParams > 6) {
                paramOffset = 8 * (numParams - 6) + offset1;
                bodyAsm.append("\n\t# 为参数而扩展栈\n");
                bodyAsm.append("\tsubq\t$").append(paramOffset).append(", %rsp\n");
            }

            // 3. 设置参数
            if (numParams > 0) {
                bodyAsm.append("\n\t# 设置参数\n");
            }

            for (int i = 0; i < numParams; i++) {
                String value = values.get(i);
                String paramAddress = "";

                if (i < 6) {
                    if (value.startsWith("ref:")) {
                        paramAddress = paramRegisterq[i];
                    } else {
                        paramAddress = paramRegisterl[i];
                    }
                } else {
                    if (i == 6) {
                        paramAddress = "(%rsp)";
                    } else {
                        paramAddress = "" + ((i - 6) * 8) + "(%rsp)";
                    }
                }

                if (value.startsWith("ref:")) {
                    // 传地址
                    bodyAsm.append("\tleaq\t").append(value.substring(4)).append(", ").append(paramAddress).append("\n");
                } else {
                    bodyAsm.append("\tmovl\t").append(value).append(", ").append(paramAddress).append("\n");
                }
            }

            // 4. 调用函数
            bodyAsm.append("\n\t# 调用函数\n");
            bodyAsm.append("\tcallq\t_").append(functionName).append("\n");

            // 5. 恢复栈
            if (numParams > 6) {
                paramOffset = 8 * (numParams - 6);
                bodyAsm.append("\n\t# 收回参数的栈空间\n");
                bodyAsm.append("\taddq\t$").append(paramOffset).append(", %rsp\n");
            }
        }

        return address;
    }

    @Override
    public String visitFunctionDeclaration(ScriptParser.FunctionDeclarationContext ctx) {
        // 给所有参数确定地址

        Function function = (Function) tree.nodeToScope.get(ctx);
        for (int i = 0; i < function.params.size(); i++) {
            if (i < 6) {
                // 少于6个参数，使用寄存器
                localVars.put(function.params.get(i), paramRegisterl[i]);
            } else {
                int paramOffset = (i - 6) * 8 + 16; // 参数在栈中相对于 %rbp 的偏移量
                String paramAddress = "" + paramOffset + "(%rbp)";
                localVars.put(function.params.get(i), paramAddress);
            }
        }

        return visitFunctionBody(ctx.functionBody());
    }

    @Override
    public String visitFunctionBody(ScriptParser.FunctionBodyContext ctx) {
        String value = null;

        if (ctx.block() != null) {
            value = visitBlock(ctx.block());
        }
        return value;
    }

    @Override
    public String visitBlock(ScriptParser.BlockContext ctx) {
        return visitBlockStatements(ctx.blockStatements());
    }

    @Override
    public String visitExpressionList(ScriptParser.ExpressionListContext ctx) {
        return super.visitExpressionList(ctx);
    }

    @Override
    public String visitFormalParameter(ScriptParser.FormalParameterContext ctx) {
        return super.visitFormalParameter(ctx);
    }

    @Override
    public String visitFormalParameterList(ScriptParser.FormalParameterListContext ctx) {
        return super.visitFormalParameterList(ctx);
    }

    @Override
    public String visitFormalParameters(ScriptParser.FormalParametersContext ctx) {
        return super.visitFormalParameters(ctx);
    }

    @Override
    public String visitPrimitiveType(ScriptParser.PrimitiveTypeContext ctx) {
        return super.visitPrimitiveType(ctx);
    }

    @Override
    public String visitType(ScriptParser.TypeContext ctx) {
        return super.visitType(ctx);
    }

    /**
     * 生成过程体
     */
    private void generateProcedure(String name, StringBuilder sb) {
        // 1. 函数标签
        sb.append("\n## 过程:").append(name).append("\n");
        sb.append("\t.globl _").append(name).append("\n");
        sb.append("_").append(name).append(":\n");

        // 2. 序曲
        sb.append("\n\t# 序曲\n");
        sb.append("\tpushq\t%rbp\n");
        sb.append("\tmovq\t%rsp, %rbp\n");

        // 3. 设置栈顶，16 字节对齐
        if ((rspOffset % 16) != 0) {
            rspOffset = (rspOffset / 16 + 1) * 16;
        }
        sb.append("\n\t# 设置栈顶\n");
        sb.append("\tsubq\t$").append(rspOffset).append(", %rsp\n");

        // 4. 保存用到的寄存器的值
        saveRegisters();

        // 5. 函数体
        sb.append("\n\t# 过程体\n");
        sb.append(bodyAsm);

        // 6. 恢复受保护的寄存器的值
        restoreRegisters();

        // 7. 恢复栈顶
        sb.append("\n\t# 恢复栈顶\n");
        sb.append("\taddq\t$").append(rspOffset).append(", %rsp\n");

        // 8. 如果是 main 函数，设置返回值为 0
        if (name.equals("main")) {
            sb.append("\n\t# 返回值\n");
            sb.append("\txorl\t%eax, %eax\n");
        }

        // 9. 尾声
        sb.append("\n\t# 尾声\n");
        sb.append("\tpopq\t%rbp\n");
        sb.append("\tretq\n");

        // 10. 重置临时变量
        rspOffset = 0;
        localVars.clear();
        tmpVars.clear();
        bodyAsm = new StringBuffer();
    }

    private String allocForExpression(ScriptParser.ExpressionContext ctx) {
        String result = null;

        //复用前序表达式的存储位置
        if (ctx.bop != null && ctx.expression().size() >= 2) {
            ScriptParser.ExpressionContext left = ctx.expression(0);
            String leftAddress = tmpVars.get(left);

            if (leftAddress!= null) {
                tmpVars.put(ctx, leftAddress);  //当前节点也跟这个地址关联起来。是否可以去掉上一个节点的？
                return leftAddress;
            }
        }

        int availableRegister = getAvailableRegister();

        if (availableRegister != -1) {
            result = registersl[availableRegister];
        } else {
            rspOffset += 4;
            result = "-" + rspOffset + "%rbp";
        }
        tmpVars.put(ctx, result);
        return result;
    }

    /**
     * 获取下一个可用的寄存器的索引
     */
    private int getAvailableRegister() {
        int result = -1;

        for (int i = 0; i < registersl.length; i++) {
            String r = registersl[i];

            if (!tmpVars.containsValue(r)) {
                result = i;
                break;
            }
        }
        return result;
    }

    private String getStringLiteralAddress(String str){
        int index = stringLiterals.indexOf(str);

        if (index == -1) {
            stringLiterals.add(str);
            index = stringLiterals.size()-1;
        }

        return "ref:L.str." + index + "(%rip)";
    }

    // 保存调用者需要保护的寄存器
    // 前提，是某寄存器被用过
    private void saveRegisters() {}

    // 恢复被保护的寄存器
    private void restoreRegisters() {}
}