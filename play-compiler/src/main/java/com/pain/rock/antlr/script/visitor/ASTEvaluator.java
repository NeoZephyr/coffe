package com.pain.rock.antlr.script.visitor;

import com.pain.rock.antlr.script.*;
import com.pain.rock.antlr.script.symbol.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ASTEvaluator extends ScriptBaseVisitor<Object> {
    private AnnotatedTree tree = null;

    protected boolean traceStackFrame = false;
    protected boolean traceFunctionCall = false;

    private Stack<StackFrame> stack = new Stack<StackFrame>();

    public ASTEvaluator(AnnotatedTree tree) {
        this.tree = tree;
    }

    @Override
    public Object visitBlock(ScriptParser.BlockContext ctx) {
        Block scope = (Block) tree.scopeOfNode(ctx);

        if (scope != null) {
            StackFrame sf = new StackFrame(scope);
            pushStack(sf);
        }

        Object object = visitBlockStatements(ctx.blockStatements());

        if (scope != null) {
            popStack();
        }

        return object;
    }

    @Override
    public Object visitBlockStatement(ScriptParser.BlockStatementContext ctx) {
        Object object = null;

        if (ctx.variableDeclarators() != null) {
            object = visitVariableDeclarators(ctx.variableDeclarators());
        } else if (ctx.statement() != null) {
            object = visitStatement(ctx.statement());
        }

        return object;
    }

    @Override
    public Object visitExpression(ScriptParser.ExpressionContext ctx) {
        Object object = null;

        if (ctx.bop != null && ctx.expression().size() >= 2) {
            Object left = visitExpression(ctx.expression(0));
            Object right = visitExpression(ctx.expression(1));
            Object leftObject = left;
            Object rightObject = right;

            if (left instanceof LeftValue) {
                leftObject = ((LeftValue) left).getValue();
            }

            if (right instanceof LeftValue) {
                rightObject = ((LeftValue) right).getValue();
            }

            Type type = tree.nodeToType.get(ctx);
            Type type1 = tree.nodeToType.get(ctx.expression(0));
            Type type2 = tree.nodeToType.get(ctx.expression(1));

            switch (ctx.bop.getType()) {
                case ScriptParser.ADD:
                    object = add(leftObject, rightObject, type);
                    break;
                case ScriptParser.SUB:
                    object = minus(leftObject, rightObject, type);
                    break;
                case ScriptParser.MUL:
                    object = mul(leftObject, rightObject, type);
                    break;
                case ScriptParser.DIV:
                    object = div(leftObject, rightObject, type);
                    break;
                case ScriptParser.EQUAL:
                    object = eq(leftObject, rightObject, PrimitiveType.getUpperType(type1, type2));
                    break;
                case ScriptParser.NOTEQUAL:
                    object = !eq(leftObject, rightObject, PrimitiveType.getUpperType(type1, type2));
                    break;
                case ScriptParser.LE:
                    object = le(leftObject, rightObject, PrimitiveType.getUpperType(type1, type2));
                    break;
                case ScriptParser.LT:
                    object = lt(leftObject, rightObject, PrimitiveType.getUpperType(type1, type2));
                    break;
                case ScriptParser.GE:
                    object = ge(leftObject, rightObject, PrimitiveType.getUpperType(type1, type2));
                    break;
                case ScriptParser.GT:
                    object = gt(leftObject, rightObject, PrimitiveType.getUpperType(type1, type2));
                    break;
                case ScriptParser.AND:
                    object = (Boolean) leftObject && (Boolean) rightObject;
                    break;
                case ScriptParser.OR:
                    object = (Boolean) leftObject || (Boolean) rightObject;
                    break;
                case ScriptParser.ASSIGN:
                    if (left instanceof LeftValue) {
                        ((LeftValue) left).setValue(rightObject);
                        object = right;
                    } else {
                        System.out.println("Unsupported feature during assignment");
                    }
                    break;
                default:
                    break;
            }
        } else if (ctx.bop != null && ctx.bop.getType() == ScriptParser.DOT) {
            Object leftObject = visitExpression(ctx.expression(0));

            if (leftObject instanceof LeftValue) {
                Object value = ((LeftValue) leftObject).getValue();

                if (value instanceof KlassObject) {
                    KlassObject container = (KlassObject) value;
                    Variable leftVariable = (Variable) tree.nodeToSymbol.get(ctx.expression(0));

                    if (ctx.IDENTIFIER() != null) {
                        Variable variable = (Variable) tree.nodeToSymbol.get(ctx);

                        if (!(leftVariable instanceof This || leftVariable instanceof Super)) {
                            variable = tree.lookupVariable(container.type, variable.getName());
                        }

                        LeftValue leftValue = new DefaultValue(container, variable);
                        object = leftValue;
                    } else if (ctx.functionCall() != null) {
                        if (traceFunctionCall) {
                            System.out.println("\n>>MethodCall : " + ctx.getText());
                        }

                        object = methodCall(container, ctx.functionCall(), (leftVariable instanceof Super));
                    }
                }
            } else {
                System.out.println("Expecting an Object Reference");
            }
        } else if (ctx.primary() != null) {
            object = visitPrimary(ctx.primary());
        } else if (ctx.postfix != null) { // 后缀运算
            Object value = visitExpression(ctx.expression(0));
            LeftValue leftValue = null;
            Type type = tree.nodeToType.get(ctx.expression(0));

            if (value instanceof LeftValue) {
                leftValue = (LeftValue) value;
                value = leftValue.getValue();
            }

            switch (ctx.postfix.getType()) {
                case ScriptParser.INC:
                    if (type == PrimitiveType.Integer) {
                        leftValue.setValue((Integer) value + 1);
                    } else {
                        leftValue.setValue((Long) value + 1);
                    }

                    object = value;
                    break;
                case ScriptParser.DEC:
                    if (type == PrimitiveType.Integer) {
                        leftValue.setValue((Integer) value - 1);
                    } else {
                        leftValue.setValue((Long) value - 1);
                    }

                    object = value;
                    break;
                default:
                    break;
            }
        } else if (ctx.prefix != null) {
            Object value = visitExpression(ctx.expression(0));
            LeftValue leftValue = null;
            Type type = tree.nodeToType.get(ctx.expression(0));

            if (value instanceof LeftValue) {
                leftValue = (LeftValue) value;
                value = leftValue.getValue();
            }

            switch (ctx.prefix.getType()) {
                case ScriptParser.INC:
                    if (type == PrimitiveType.Integer) {
                        object = (Integer) value + 1;
                    } else {
                        object = (Long) value + 1;
                    }
                    leftValue.setValue(object);
                    break;
                case ScriptParser.DEC:
                    if (type == PrimitiveType.Integer) {
                        object = (Integer) value - 1;
                    } else {
                        object = (Long) value - 1;
                    }
                    leftValue.setValue(object);
                    break;
                case ScriptParser.BANG:
                    object = !((Boolean) value);
                    break;
                default:
                    break;
            }
        } else if (ctx.functionCall() != null) {
            object = visitFunctionCall(ctx.functionCall());
        }

        return object;
    }

    @Override
    public Object visitExpressionList(ScriptParser.ExpressionListContext ctx) {
        Object object = null;

        for (ScriptParser.ExpressionContext child : ctx.expression()) {
            object = visitExpression(child);
        }

        return object;
    }

    @Override
    public Object visitForInit(ScriptParser.ForInitContext ctx) {
        Object object = null;

        if (ctx.variableDeclarators() != null) {
            object = visitVariableDeclarators(ctx.variableDeclarators());
        } else if (ctx.expressionList() != null) {
            object = visitExpressionList(ctx.expressionList());
        }

        return object;
    }

    @Override
    public Object visitLiteral(ScriptParser.LiteralContext ctx) {
        Object object = null;

        if (ctx.integerLiteral() != null) {
            object = visitIntegerLiteral(ctx.integerLiteral());
        } else if (ctx.floatLiteral() != null) {
            object = visitFloatLiteral(ctx.floatLiteral());
        } else if (ctx.BOOL_LITERAL() != null) {
            if (ctx.BOOL_LITERAL().getText().equals("true")) {
                object = Boolean.TRUE;
            } else {
                object = Boolean.FALSE;
            }
        } else if (ctx.STRING_LITERAL() != null) {
            String withQuotationMark = ctx.STRING_LITERAL().getText();
            object = withQuotationMark.substring(1, withQuotationMark.length() - 1);
        } else if (ctx.CHAR_LITERAL() != null) {
            object = ctx.CHAR_LITERAL().getText().charAt(0);
        } else if (ctx.NULL_LITERAL() != null) {
            object = NullObject.instance();
        }

        return object;
    }

    @Override
    public Object visitIntegerLiteral(ScriptParser.IntegerLiteralContext ctx) {
        Object object = null;

        if (ctx.DECIMAL_LITERAL() != null) {
            object = Integer.valueOf(ctx.DECIMAL_LITERAL().getText());
        }

        return object;
    }

    @Override
    public Object visitFloatLiteral(ScriptParser.FloatLiteralContext ctx) {
        return Float.valueOf(ctx.getText());
    }

    @Override
    public Object visitParExpression(ScriptParser.ParExpressionContext ctx) {
        return visitExpression(ctx.expression());
    }

    @Override
    public Object visitPrimary(ScriptParser.PrimaryContext ctx) {
        Object object = null;

        if (ctx.literal() != null) {
            object = visitLiteral(ctx.literal());
        } else if (ctx.IDENTIFIER() != null) {
            Symbol symbol = tree.nodeToSymbol.get(ctx);

            if (symbol instanceof Variable) {
                object = getLeftValue((Variable) symbol);
            } else if (symbol instanceof Function) {
                FunctionObject fo = new FunctionObject((Function) symbol);
                object = fo;
            }
        } else if (ctx.expression() != null) {
            object = visitExpression(ctx.expression());
        } else if (ctx.THIS() != null) {
            This thisRef = (This) tree.nodeToSymbol.get(ctx);
            object = getLeftValue(thisRef);
        } else if (ctx.SUPER() != null) {
            Super superRef = (Super) tree.nodeToSymbol.get(ctx);
            object = getLeftValue(superRef);
        }

        return object;
    }

    @Override
    public Object visitPrimitiveType(ScriptParser.PrimitiveTypeContext ctx) {
        Object object = null;

        if (ctx.INT() != null) {
            object = ScriptParser.INT;
        } else if (ctx.LONG() != null) {
            object = ScriptParser.LONG;
        } else if (ctx.FLOAT() != null) {
            object = ScriptParser.FLOAT;
        } else if (ctx.DOUBLE() != null) {
            object = ScriptParser.DOUBLE;
        } else if (ctx.BOOLEAN() != null) {
            object = ScriptParser.BOOLEAN;
        } else if (ctx.CHAR() != null) {
            object = ScriptParser.CHAR;
        } else if (ctx.SHORT() != null) {
            object = ScriptParser.SHORT;
        } else if (ctx.BYTE() != null) {
            object = ScriptParser.BYTE;
        }

        return object;
    }

    @Override
    public Object visitStatement(ScriptParser.StatementContext ctx) {
        Object object = null;

        if (ctx.statementExpression != null) {
            object = visitExpression(ctx.statementExpression);
        } else if (ctx.IF() != null) {
            Boolean condition = (Boolean) visitParExpression(ctx.parExpression());

            if (Boolean.TRUE == condition) {
                object = visitStatement(ctx.statement(0));
            } else if (ctx.ELSE() != null) {
                object = visitStatement(ctx.statement(1));
            }
        } else if (ctx.WHILE() != null) {
            if (ctx.parExpression().expression() != null && ctx.statement(0) != null) {
                while (true) {
                    Boolean condition = true;
                    Object value = visitExpression(ctx.parExpression().expression());

                    if (value instanceof LeftValue) {
                        condition = (Boolean) ((LeftValue) value).getValue();
                    } else {
                        condition = (Boolean) value;
                    }

                    if (condition) {
                        object = visitStatement(ctx.statement(0));

                        if (object instanceof BreakObject) {
                            object = null;
                            break;
                        } else if (object instanceof ReturnObject) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        } else if (ctx.FOR() != null) {
            Block scope = (Block) tree.scopeOfNode(ctx);
            StackFrame sf = new StackFrame(scope);
            pushStack(sf);

            ScriptParser.ForControlContext fcc = ctx.forControl();

            if (fcc.enhancedForControl() != null) {

            } else {
                if (fcc.forInit() != null) {
                    object = visitForInit(fcc.forInit());
                }

                while (true) {
                    Boolean condition = true;

                    if (fcc.expression() != null) {
                        Object value = visitExpression(fcc.expression());

                        if (value instanceof LeftValue) {
                            condition = (Boolean) ((LeftValue) value).getValue();
                        } else {
                            condition = (Boolean) value;
                        }
                    }

                    if (condition) {
                        object = visitStatement(ctx.statement(0));

                        if (object instanceof BreakObject) {
                            object = null;
                            break;
                        } else if (object instanceof ReturnObject) {
                            break;
                        }

                        if (fcc.forUpdate != null) {
                            visitExpressionList(fcc.forUpdate);
                        }
                    } else {
                        break;
                    }
                }
            }

            popStack();
        } else if (ctx.blockLabel != null) {
            object = visitBlock(ctx.blockLabel);
        } else if (ctx.BREAK() != null) {
            object = BreakObject.instance();
        } else if (ctx.RETURN() != null) {
            if (ctx.expression() != null) {
                object = visitExpression(ctx.expression());

                if (object instanceof LeftValue) {
                    object = ((LeftValue) object).getValue();
                }

                if (object instanceof FunctionObject) {
                    FunctionObject fo = (FunctionObject) object;
                    getClosureValues(fo.function, fo);
                } else if (object instanceof KlassObject) {
                    KlassObject klassObject = (KlassObject) object;
                    getClosureValues(klassObject);
                }
            }

            object = new ReturnObject(object);
        }

        return object;
    }

    @Override
    public Object visitTypeType(ScriptParser.TypeTypeContext ctx) {
        return visitPrimitiveType(ctx.primitiveType());
    }

    @Override
    public Object visitVariableDeclarator(ScriptParser.VariableDeclaratorContext ctx) {
        Object object = null;
        LeftValue leftValue = (LeftValue) visitVariableDeclaratorId(ctx.variableDeclaratorId());

        if (ctx.variableInitializer() != null) {
            object = visitVariableInitializer(ctx.variableInitializer());

            if (object instanceof LeftValue) {
                object = ((LeftValue) object).getValue();
            }

            leftValue.setValue(object);
        }

        return object;
    }

    @Override
    public Object visitVariableDeclaratorId(ScriptParser.VariableDeclaratorIdContext ctx) {
        Object object = null;
        Symbol symbol = tree.nodeToSymbol.get(ctx);
        object = getLeftValue((Variable) symbol);
        return object;
    }

    @Override
    public Object visitVariableDeclarators(ScriptParser.VariableDeclaratorsContext ctx) {
        Object object = null;

        for (ScriptParser.VariableDeclaratorContext child : ctx.variableDeclarator()) {
            object = visitVariableDeclarator(child);
        }

        return object;
    }

    @Override
    public Object visitVariableInitializer(ScriptParser.VariableInitializerContext ctx) {
        Object object = null;

        if (ctx.expression() != null) {
            object = visitExpression(ctx.expression());
        }

        return object;
    }

    @Override
    public Object visitBlockStatements(ScriptParser.BlockStatementsContext ctx) {
        Object object = null;

        for (ScriptParser.BlockStatementContext child : ctx.blockStatement()) {
            object = visitBlockStatement(child);

            if (object instanceof BreakObject) {
                break;
            } else if (object instanceof ReturnObject) {
                break;
            }
        }

        return object;
    }

    @Override
    public Object visitProg(ScriptParser.ProgContext ctx) {
        Object object = null;
        pushStack(new StackFrame((Block) tree.nodeToScope.get(ctx)));
        object = visitBlockStatements(ctx.blockStatements());
        popStack();
        return object;
    }

    @Override
    public Object visitFunctionCall(ScriptParser.FunctionCallContext ctx) {
        if (ctx.THIS() != null) {
            thisConstructor(ctx);
            return null;
        } else if (ctx.SUPER() != null) {
            thisConstructor(ctx);
            return null;
        }

        Object object = null;

        // 调用时的名称，不一定是真正的函数名，还可能是函数尅性的变量名
        String funcName = ctx.IDENTIFIER().getText();
        Symbol symbol = tree.nodeToSymbol.get(ctx);

        if (symbol instanceof DefaultConstructor) {
            return createAndInitKlassObject(((DefaultConstructor) symbol).klass());
        } else if (funcName.equals("println")) {
            println(ctx);
            return object;
        }

        FunctionObject fo = getFunctionObject(ctx);
        Function func = fo.function;

        if (func.isConstructor()) {
            Klass klass = (Klass) func.getEnclosingScope();
            KlassObject newObject = createAndInitKlassObject(klass);
            methodCall(newObject, ctx, false);
            return newObject;
        }

        List<Object> paramValues = calcParamValues(ctx);

        if (traceFunctionCall) {
            System.out.println("\n>>FunctionCall : " + ctx.getText());
        }

        object = functionCall(fo, paramValues);
        return object;
    }

    private List<Object> calcParamValues(ScriptParser.FunctionCallContext ctx) {
        List<Object> paramValues = new ArrayList<>();

        if (ctx.expressionList() != null) {
            for (ScriptParser.ExpressionContext exp : ctx.expressionList().expression()) {
                Object value = visitExpression(exp);

                if (value instanceof LeftValue) {
                    value = ((LeftValue) value).getValue();
                }

                paramValues.add(value);
            }
        }

        return paramValues;
    }

    private FunctionObject getFunctionObject(ScriptParser.FunctionCallContext ctx) {
        if (ctx.IDENTIFIER() == null) {
            return null;
        }

        Function function = null;
        FunctionObject fo = null;
        Symbol symbol = tree.nodeToSymbol.get(ctx);

        if (symbol instanceof Variable) {
            Variable variable = (Variable) symbol;
            LeftValue leftValue = getLeftValue(variable);
            Object value = leftValue.getValue();

            if (value instanceof FunctionObject) {
                fo = (FunctionObject) value;
                function = fo.function;
            }
        } else if (symbol instanceof Function) {
            function = (Function) symbol;
        } else {
            String funcName = ctx.IDENTIFIER().getText();
            tree.log("unable to find function or function variable " + funcName, ctx);
            return null;
        }

        if (fo == null) {
            fo = new FunctionObject(function);
        }

        return fo;
    }

    private Object functionCall(FunctionObject fo, List<Object> paramValues) {
        Object object = null;
        StackFrame frame = new StackFrame(fo);
        pushStack(frame);
        ScriptParser.FunctionDeclarationContext fdc = (ScriptParser.FunctionDeclarationContext) fo.function.ctx;

        if (fdc.formalParameters().formalParameterList() != null) {
            for (int i = 0; i < fdc.formalParameters().formalParameterList().formalParameter().size(); i++) {
                ScriptParser.FormalParameterContext param = fdc.formalParameters().formalParameterList().formalParameter(i);
                LeftValue leftValue = (LeftValue) visitVariableDeclaratorId(param.variableDeclaratorId());
                leftValue.setValue(paramValues.get(i));
            }
        }

        object = visitFunctionDeclaration(fdc);
        popStack();

        if (object instanceof ReturnObject) {
            object = ((ReturnObject) object).returnValue;
        }

        return object;
    }

    private Object methodCall(KlassObject ko, ScriptParser.FunctionCallContext ctx, boolean isSuper) {
        Object object = null;
        StackFrame frame = new StackFrame(ko);
        pushStack(frame);
        FunctionObject fo = getFunctionObject(ctx);
        popStack();
        Function function = fo.function;
        Klass klass = ko.type;

        if (!function.isConstructor() && !isSuper) {
            Function overrideFunc = klass.getFunction(function.name, function.getParamTypes());

            if (overrideFunc != null && overrideFunc != function) {
                function = overrideFunc;
                fo.function = function;
            }
        }

        List<Object> paramValues = calcParamValues(ctx);
        pushStack(frame);
        object = functionCall(fo, paramValues);
        popStack();
        return object;
    }

    private void thisConstructor(ScriptParser.FunctionCallContext ctx) {
        Symbol symbol = tree.nodeToSymbol.get(ctx);

        if (symbol instanceof DefaultConstructor) {
            return;
        } else if (symbol instanceof Function) {
            Function function = (Function) symbol;
            FunctionObject fo = new FunctionObject(function);
            List<Object> paramValues = calcParamValues(ctx);
            functionCall(fo, paramValues);
        }
    }

    @Override
    public Object visitFunctionDeclaration(ScriptParser.FunctionDeclarationContext ctx) {
        return visitFunctionBody(ctx.functionBody());
    }

    @Override
    public Object visitFunctionBody(ScriptParser.FunctionBodyContext ctx) {
        Object object = null;

        if (ctx.block() != null) {
            object = visitBlock(ctx.block());
        }

        return object;
    }

    @Override
    public Object visitClassBody(ScriptParser.ClassBodyContext ctx) {
        Object object = null;

        for (ScriptParser.ClassBodyDeclarationContext child : ctx.classBodyDeclaration()) {
            object = visitClassBodyDeclaration(child);
        }

        return object;
    }

    @Override
    public Object visitClassBodyDeclaration(ScriptParser.ClassBodyDeclarationContext ctx) {
        Object object = null;

        if (ctx.memberDeclaration() != null) {
            object = visitMemberDeclaration(ctx.memberDeclaration());
        }

        return object;
    }

    @Override
    public Object visitMemberDeclaration(ScriptParser.MemberDeclarationContext ctx) {
        Object object = null;

        if (ctx.fieldDeclaration() != null) {
            object = visitFieldDeclaration(ctx.fieldDeclaration());
        }

        return object;
    }

    @Override
    public Object visitFieldDeclaration(ScriptParser.FieldDeclarationContext ctx) {
        Object object = null;

        if (ctx.variableDeclarators() != null) {
            object = visitVariableDeclarators(ctx.variableDeclarators());
        }

        return object;
    }

    private void pushStack(StackFrame frame) {
        if (stack.size() > 0) {
            // 从栈顶到栈底依次查找
            for (int i = stack.size() - 1; i > 0; i--) {
                StackFrame sf = stack.get(i);

                if (sf.scope.getEnclosingScope() == frame.scope.getEnclosingScope()) { // 跟某个已有的栈桢的 enclosingScope 是一样的
                    frame.parentFrame = sf.parentFrame;
                    break;
                } else if (sf.scope == frame.scope.getEnclosingScope()) { // 新加入的栈桢，是某个已有的栈桢的下一级
                    frame.parentFrame = sf;
                    break;
                } else if (frame.object instanceof FunctionObject) {
                    FunctionObject fo = (FunctionObject) frame.object;

                    if (fo.variable != null && fo.variable.getEnclosingScope() == sf.scope) {
                        frame.parentFrame = sf;
                        break;
                    }
                }
            }

            if (frame.parentFrame == null) {
                frame.parentFrame = stack.peek();
            }
        }

        stack.push(frame);

        if (traceStackFrame) {
            dumpStackFrame();
        }
    }

    public LeftValue getLeftValue(Variable variable) {
        StackFrame sf = stack.peek();
        PlayObject po = null;

        while (sf != null) {
            if (sf.scope.containsSymbol(variable)) {
                po = sf.object;
                break;
            }

            sf = sf.parentFrame;
        }

        // 从闭包里找
        if (po == null) {
            sf = stack.peek();

            while (sf != null) {
                if (sf.contains(variable)) {
                    po = sf.object;
                    break;
                }
                sf = sf.parentFrame;
            }
        }

        DefaultValue lvalue = new DefaultValue(po, variable);
        return lvalue;
    }

    private void popStack() {
        stack.pop();
    }

    private void dumpStackFrame() {
        System.out.println("\nStack Frames ----------------");

        for (StackFrame frame : stack) {
            System.out.println(frame);
        }

        System.out.println("-----------------------------\n");
    }

    /**
     * 为闭包获取环境变量的值
     */
    private void getClosureValues(Function function, PlayObject container) {
        if (function.closureVariables != null) {
            for (Variable variable : function.closureVariables) {
                LeftValue leftValue = getLeftValue(variable);
                Object value = leftValue.getValue();
                container.fields.put(variable, value);
            }
        }
    }

    private void getClosureValues(KlassObject klassObject) {
        PlayObject object = new PlayObject();

        for (Variable variable : klassObject.fields.keySet()) {
            if (variable.type instanceof FunctionType) {
                Object o = klassObject.fields.get(variable);

                if (o != null) {
                    FunctionObject fo = (FunctionObject) o;
                    getClosureValues(fo.function, object);
                }
            }
        }

        klassObject.fields.putAll(object.fields);
    }

    protected KlassObject createAndInitKlassObject(Klass klass) {
        KlassObject object = new KlassObject();
        object.type = klass;
        Stack<Klass> ancestorChain = new Stack<>();
        ancestorChain.push(klass);

        while (klass.getParentKlass() != null) {
            ancestorChain.push(klass.getParentKlass());
            klass = klass.getParentKlass();
        }

        StackFrame frame = new StackFrame(object);
        pushStack(frame);

        while (ancestorChain.size() > 0) {
            Klass k = ancestorChain.pop();
            defaultObjectInit(k, object);
        }

        popStack();
        return object;
    }

    protected void defaultObjectInit(Klass klass, KlassObject object) {
        for (Symbol symbol : klass.getSymbols()) {
            if (symbol instanceof Variable) {
                object.fields.put((Variable) symbol, null);
            }
        }

        ScriptParser.ClassBodyContext ctx = ((ScriptParser.ClassDeclarationContext) klass.ctx).classBody();
        visitClassBody(ctx);
    }

    private void println(ScriptParser.FunctionCallContext ctx) {
        if (ctx.expressionList() != null) {
            Object value = visitExpressionList(ctx.expressionList());

            if (value instanceof LeftValue) {
                value = ((LeftValue) value).getValue();
            }

            System.out.println(value);
        } else {
            System.out.println();
        }
    }

    private Object add(Object obj1, Object obj2, Type targetType) {
        Object result = null;

        if (targetType == PrimitiveType.String) {
            result = String.valueOf(obj1) + String.valueOf(obj2);
        } else if (targetType == PrimitiveType.Integer) {
            result = ((Number) obj1).intValue() + ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            result = ((Number) obj1).floatValue() + ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            result = ((Number) obj1).longValue() + ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            result = ((Number) obj1).doubleValue() + ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            result = ((Number) obj1).shortValue() + ((Number) obj2).shortValue();
        } else {
            System.out.println("unsupported add operation");
        }

        return result;
    }

    private Object minus(Object obj1, Object obj2, Type targetType) {
        Object result = null;

        if (targetType == PrimitiveType.Integer) {
            result = ((Number) obj1).intValue() - ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            result = ((Number) obj1).floatValue() - ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            result = ((Number) obj1).longValue() - ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            result = ((Number) obj1).doubleValue() - ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            result = ((Number) obj1).shortValue() - ((Number) obj2).shortValue();
        } else {
            System.out.println("unsupported minus operation");
        }

        return result;
    }

    private Object mul(Object obj1, Object obj2, Type targetType) {
        Object result = null;

        if (targetType == PrimitiveType.Integer) {
            result = ((Number) obj1).intValue() * ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            result = ((Number) obj1).floatValue() * ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            result = ((Number) obj1).longValue() * ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            result = ((Number) obj1).doubleValue() * ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            result = ((Number) obj1).shortValue() * ((Number) obj2).shortValue();
        } else {
            System.out.println("unsupported mul operation");
        }

        return result;
    }

    private Object div(Object obj1, Object obj2, Type targetType) {
        Object result = null;

        if (targetType == PrimitiveType.Integer) {
            result = ((Number) obj1).intValue() / ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            result = ((Number) obj1).floatValue() / ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            result = ((Number) obj1).longValue() / ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            result = ((Number) obj1).doubleValue() / ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            result = ((Number) obj1).shortValue() / ((Number) obj2).shortValue();
        } else {
            System.out.println("unsupported div operation");
        }

        return result;
    }

    private boolean eq(Object obj1, Object obj2, Type targetType) {
        Object result = null;

        if (targetType == PrimitiveType.Integer) {
            result = ((Number) obj1).intValue() == ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            result = ((Number) obj1).floatValue() == ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            result = ((Number) obj1).longValue() == ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            result = ((Number) obj1).doubleValue() == ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            result = ((Number) obj1).shortValue() == ((Number) obj2).shortValue();
        } else {
            result = (obj1 == obj2);
        }

        return (boolean) result;
    }

    private Object ge(Object obj1, Object obj2, Type targetType) {
        Object result = null;

        if (targetType == PrimitiveType.Integer) {
            result = ((Number) obj1).intValue() >= ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            result = ((Number) obj1).floatValue() >= ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            result = ((Number) obj1).longValue() >= ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            result = ((Number) obj1).doubleValue() >= ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            result = ((Number) obj1).shortValue() >= ((Number) obj2).shortValue();
        } else {
            System.out.println("unsupported ge operation");
        }

        return result;
    }

    private Object gt(Object obj1, Object obj2, Type targetType) {
        Object result = null;

        if (targetType == PrimitiveType.Integer) {
            result = ((Number) obj1).intValue() > ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            result = ((Number) obj1).floatValue() > ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            result = ((Number) obj1).longValue() > ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            result = ((Number) obj1).doubleValue() > ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            result = ((Number) obj1).shortValue() > ((Number) obj2).shortValue();
        } else {
            System.out.println("unsupported gt operation");
        }

        return result;
    }

    private Object le(Object obj1, Object obj2, Type targetType) {
        Object result = null;

        if (targetType == PrimitiveType.Integer) {
            result = ((Number) obj1).intValue() <= ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            result = ((Number) obj1).floatValue() <= ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            result = ((Number) obj1).longValue() <= ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            result = ((Number) obj1).doubleValue() <= ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            result = ((Number) obj1).shortValue() <= ((Number) obj2).shortValue();
        } else {
            System.out.println("unsupported le operation");
        }

        return result;
    }

    private Object lt(Object obj1, Object obj2, Type targetType) {
        Object result = null;

        if (targetType == PrimitiveType.Integer) {
            result = ((Number) obj1).intValue() < ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            result = ((Number) obj1).floatValue() < ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            result = ((Number) obj1).longValue() < ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            result = ((Number) obj1).doubleValue() < ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            result = ((Number) obj1).shortValue() < ((Number) obj2).shortValue();
        } else {
            System.out.println("unsupported lt operation");
        }

        return result;
    }

    private final class DefaultValue implements LeftValue {
        private Variable variable;
        private PlayObject container;

        public DefaultValue(PlayObject container, Variable variable) {
            this.container = container;
            this.variable = variable;
        }

        @Override
        public Object getValue() {
            if (variable instanceof This || variable instanceof Super) {
                return container;
            }

            return container.getValue(variable);
        }

        @Override
        public void setValue(Object value) {
            container.setValue(variable, value);

            if (value instanceof FunctionObject) {
                ((FunctionObject) value).variable = variable;
            }
        }

        @Override
        public Variable getVariable() {
            return variable;
        }

        @Override
        public String toString() {
            return "LeftValue of " + variable.name + " : " + getValue();
        }

        @Override
        public PlayObject getValueContainer() {
            return container;
        }
    }
}
