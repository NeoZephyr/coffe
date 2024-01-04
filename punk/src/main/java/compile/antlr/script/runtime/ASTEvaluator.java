package compile.antlr.script.runtime;

import compile.antlr.script.ScriptBaseVisitor;
import compile.antlr.script.ScriptParser;
import compile.antlr.script.analyzer.AnnotatedTree;
import compile.antlr.script.memory.*;
import compile.antlr.script.symbol.*;
import compile.antlr.script.types.FunctionType;
import compile.antlr.script.types.PrimitiveType;
import compile.antlr.script.types.Type;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class ASTEvaluator extends ScriptBaseVisitor<Object> {

    public AnnotatedTree tree = null;

    public boolean traceStackFrame = false;

    public boolean traceFunctionCall = false;

    // 栈桢的管理
    public Stack<StackFrame> stack = new Stack<>();

    public ASTEvaluator(AnnotatedTree tree) {
        this.tree = tree;
    }

    public ValueRef getValueRef(Variable variable) {
        StackFrame frame = stack.peek();
        ActivationRecord record = null;

        while (frame != null) {
            if (frame.scope.containsSymbol(variable)) {
                record = frame.record;
                break;
            }

            frame = frame.parent;
        }

        // 通过正常的作用域找不到，就从闭包里找
        if (record == null) {
            frame = stack.peek();

            while (frame != null) {
                if (frame.contains(variable)) {
                    record = frame.record;
                    break;
                }
                frame = frame.parent;
            }
        }

        return new ValueRef(record, variable);
    }

    @Override
    public Object visitProg(ScriptParser.ProgContext ctx) {
        Object obj = null;
        pushStack(new StackFrame((Block) tree.nodeToScope.get(ctx)));
        obj = visitBlockStatements(ctx.blockStatements());
        popStack();
        return obj;
    }

    @Override
    public Object visitBlock(ScriptParser.BlockContext ctx) {
        Block block = (Block) tree.nodeToScope.get(ctx);

        // 有些 block 是不对应 scope 的，比如函数底下的 block
        if (block != null) {
            StackFrame frame = new StackFrame(block);
            pushStack(frame);
        }

        Object obj = visitBlockStatements(ctx.blockStatements());

        if (block != null) {
            popStack();
        }

        return obj;
    }

    @Override
    public Object visitBlockStatement(ScriptParser.BlockStatementContext ctx) {
        Object obj = null;

        if (ctx.variableDeclarators() != null) {
            obj = visitVariableDeclarators(ctx.variableDeclarators());
        } else if (ctx.statement() != null) {
            obj = visitStatement(ctx.statement());
        }

        return obj;
    }

    @Override
    public Object visitExpression(ScriptParser.ExpressionContext ctx) {
        Object obj = null;

        if ((ctx.bop != null) && (ctx.expression().size() >= 2)) {
            Object left = visitExpression(ctx.expression(0));
            Object right = visitExpression(ctx.expression(1));
            Object leftValue = left;
            Object rightValue = right;

            if (left instanceof ValueRef) {
                leftValue = ((ValueRef) left).getValue();
            }

            if (right instanceof ValueRef) {
                rightValue = ((ValueRef) right).getValue();
            }

            Type type = tree.nodeToType.get(ctx);
            Type type1 = tree.nodeToType.get(ctx.expression(0));
            Type type2 = tree.nodeToType.get(ctx.expression(1));

            switch (ctx.bop.getType()) {
                case ScriptParser.ADD:
                    obj = add(leftValue, rightValue, type);
                    break;
                case ScriptParser.SUB:
                    obj = sub(leftValue, rightValue, type);
                    break;
                case ScriptParser.MUL:
                    obj = mul(leftValue, rightValue, type);
                    break;
                case ScriptParser.DIV:
                    obj = div(leftValue, rightValue, type);
                    break;
                case ScriptParser.EQUAL:
                    obj = eq(leftValue, rightValue, PrimitiveType.upperType(type1, type2));
                    break;
                case ScriptParser.NOTEQUAL:
                    obj = !eq(leftValue, rightValue, PrimitiveType.upperType(type1, type2));
                    break;
                case ScriptParser.LE:
                    obj = le(leftValue, rightValue, PrimitiveType.upperType(type1, type2));
                    break;
                case ScriptParser.LT:
                    obj = lt(leftValue, rightValue, PrimitiveType.upperType(type1, type2));
                    break;
                case ScriptParser.GE:
                    obj = ge(leftValue, rightValue, PrimitiveType.upperType(type1, type2));
                    break;
                case ScriptParser.GT:
                    obj = gt(leftValue, rightValue, PrimitiveType.upperType(type1, type2));
                    break;
                case ScriptParser.AND:
                    obj = (Boolean) leftValue && (Boolean) rightValue;
                    break;
                case ScriptParser.OR:
                    obj = (Boolean) leftValue || (Boolean) rightValue;
                    break;
                case ScriptParser.ASSIGN:
                    if (left instanceof ValueRef) {
                        ((ValueRef) left).setValue(rightValue);
                        obj = right;
                    } else {
                        System.out.println("Unsupported feature during assignment");
                    }
                    break;
                default:
                    break;
            }
        } else if ((ctx.bop != null) && (ctx.bop.getType() == ScriptParser.DOT)) {
            Object left = visitExpression(ctx.expression(0));

            if (left instanceof ValueRef) {
                Object value = ((ValueRef) left).getValue();

                if (value instanceof KlassObject) {
                    KlassObject record = (KlassObject) value;
                    Variable leftVar = (Variable) tree.nodeToSymbol.get(ctx.expression(0));

                    if (ctx.IDENTIFIER() != null) {
                        Variable variable = (Variable) tree.nodeToSymbol.get(ctx);

                        if (!(leftVar instanceof This || leftVar instanceof Super)) {
                            // 类的成员可能需要重载
                            variable = tree.lookupVariable(record.klass, variable.name);
                        }

                        obj = new ValueRef(record, variable);
                    } else if (ctx.functionCall() != null) {
                        if (traceFunctionCall) {
                            System.out.println("\n>>MethodCall : " + ctx.getText());
                        }

                        obj = methodCall(record, ctx.functionCall(), leftVar instanceof Super);
                    }
                }
            } else {
                System.out.println("Expecting an Object Reference");
            }
        } else if (ctx.primary() != null) {
            obj = visitPrimary(ctx.primary());
        } else if (ctx.postfix != null) {
            Object value = visitExpression(ctx.expression(0));
            Type type = tree.nodeToType.get(ctx.expression(0));
            ValueRef valueRef = null;

            if (value instanceof ValueRef) {
                valueRef = (ValueRef) value;
                value = valueRef.getValue();
            }

            switch (ctx.postfix.getType()) {
                case ScriptParser.INC:
                    if (type == PrimitiveType.Integer) {
                        valueRef.setValue((Integer) value + 1);
                    } else {
                        valueRef.setValue((Long) value + 1);
                    }
                    obj = value;
                    break;
                case ScriptParser.DEC:
                    if (type == PrimitiveType.Integer) {
                        valueRef.setValue((Integer) value - 1);
                    } else {
                        valueRef.setValue((Long) value - 1);
                    }
                    obj = value;
                    break;
                default:
                    break;
            }
        } else if (ctx.prefix != null) {
            Object value = visitExpression(ctx.expression(0));
            Type type = tree.nodeToType.get(ctx.expression(0));
            ValueRef valueRef = null;

            if (value instanceof ValueRef) {
                valueRef = (ValueRef) value;
                value = valueRef.getValue();
            }

            switch (ctx.prefix.getType()) {
                case ScriptParser.INC:
                    if (type == PrimitiveType.Integer) {
                        obj = (Integer) value + 1;
                    } else {
                        obj = (Long) value + 1;
                    }

                    valueRef.setValue(obj);
                    break;
                case ScriptParser.DEC:
                    if (type == PrimitiveType.Integer) {
                        obj = (Integer) value - 1;
                    } else {
                        obj = (Long) value - 1;
                    }

                    valueRef.setValue(obj);
                    break;
                case ScriptParser.BANG:
                    obj = !((Boolean) value);
                    break;
                default:
                    break;
            }
        } else if (ctx.functionCall() != null) {
            obj = visitFunctionCall(ctx.functionCall());
        }

        return obj;
    }

    @Override
    public Object visitExpressionList(ScriptParser.ExpressionListContext ctx) {
        Object obj = null;

        for (ScriptParser.ExpressionContext child : ctx.expression()) {
            obj = visitExpression(child);
        }
        return obj;
    }

    @Override
    public Object visitForInit(ScriptParser.ForInitContext ctx) {
        Object obj = null;

        if (ctx.variableDeclarators() != null) {
            obj = visitVariableDeclarators(ctx.variableDeclarators());
        } else if (ctx.expressionList() != null) {
            obj = visitExpressionList(ctx.expressionList());
        }

        return obj;
    }

    @Override
    public Object visitLiteral(ScriptParser.LiteralContext ctx) {
        Object obj = null;

        if (ctx.integerLiteral() != null) {
            obj = visitIntegerLiteral(ctx.integerLiteral());
        } else if (ctx.floatLiteral() != null) {
            obj = visitFloatLiteral(ctx.floatLiteral());
        } else if (ctx.BOOL_LITERAL() != null) {
            if (ctx.BOOL_LITERAL().getText().equals("true")) {
                obj = Boolean.TRUE;
            } else {
                obj = Boolean.FALSE;
            }
        } else if (ctx.STRING_LITERAL() != null) {
            String withQuota = ctx.STRING_LITERAL().getText();
            obj = withQuota.substring(1, withQuota.length() - 1);
        } else if (ctx.CHAR_LITERAL() != null) {
            obj = ctx.CHAR_LITERAL().getText().charAt(0);
        } else if (ctx.NULL_LITERAL() != null) {
            obj = KlassObject.NULL_OBJECT;
        }

        return obj;
    }

    @Override
    public Object visitIntegerLiteral(ScriptParser.IntegerLiteralContext ctx) {
        Object obj = null;

        if (ctx.DECIMAL_LITERAL() != null) {
            obj = Integer.valueOf(ctx.DECIMAL_LITERAL().getText());
        }

        return obj;
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
        Object obj = null;

        if (ctx.literal() != null) {
            obj = visitLiteral(ctx.literal());
        } else if (ctx.IDENTIFIER() != null) {
            Symbol symbol = tree.nodeToSymbol.get(ctx);

            if (symbol instanceof Variable) {
                obj = getValueRef((Variable) symbol);
            } else if (symbol instanceof Function) {
                obj = new FunctionObject((Function) symbol);
            }
        } else if (ctx.expression() != null) {
            obj = visitExpression(ctx.expression());
        } else if (ctx.THIS() != null) {
            This thisRef = (This) tree.nodeToSymbol.get(ctx);
            obj = getValueRef(thisRef);
        } else if (ctx.SUPER() != null) {
            Super superRef = (Super) tree.nodeToSymbol.get(ctx);
            obj = getValueRef(superRef);
        }

        return obj;
    }

    @Override
    public Object visitPrimitiveType(ScriptParser.PrimitiveTypeContext ctx) {
        Object obj = null;

        if (ctx.INT() != null) {
            obj = ScriptParser.INT;
        } else if (ctx.LONG() != null) {
            obj = ScriptParser.LONG;
        } else if (ctx.FLOAT() != null) {
            obj = ScriptParser.FLOAT;
        } else if (ctx.DOUBLE() != null) {
            obj = ScriptParser.DOUBLE;
        } else if (ctx.BOOLEAN() != null) {
            obj = ScriptParser.BOOLEAN;
        } else if (ctx.CHAR() != null) {
            obj = ScriptParser.CHAR;
        } else if (ctx.SHORT() != null) {
            obj = ScriptParser.SHORT;
        } else if (ctx.BYTE() != null) {
            obj = ScriptParser.BYTE;
        }

        return obj;
    }

    @Override
    public Object visitStatement(ScriptParser.StatementContext ctx) {
        Object obj = null;

        if (ctx.statementExpression != null) {
            obj = visitExpression(ctx.statementExpression);
        } else if (ctx.IF() != null) {
            Boolean condition = (Boolean) visitParExpression(ctx.parExpression());

            if (Boolean.TRUE == condition) {
                obj = visitStatement(ctx.statement(0));
            } else if (ctx.ELSE() != null) {
                obj = visitStatement(ctx.statement(1));
            }
        } else if (ctx.WHILE() != null) {
            if ((ctx.parExpression().expression() != null) && (ctx.statement(0) != null)) {
                while (true) {
                    Boolean condition = true;
                    Object value = visitExpression(ctx.parExpression().expression());

                    if (value instanceof ValueRef) {
                        condition = (Boolean) ((ValueRef) value).getValue();
                    } else {
                        condition = (Boolean) value;
                    }

                    if (condition) {
                        obj = visitStatement(ctx.statement(0));

                        if (obj instanceof BreakObject) {
                            obj = null;
                            break;
                        } else if (obj instanceof ReturnObject) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        } else if (ctx.FOR() != null) {
            Block block = (Block) tree.nodeToScope.get(ctx);
            StackFrame frame = new StackFrame(block);
            pushStack(frame);

            ScriptParser.ForControlContext fcc = ctx.forControl();

            if (fcc.enhancedForControl() != null) {
            } else {
                if (fcc.forInit() != null) {
                    obj = visitForInit(fcc.forInit());
                }

                while (true) {
                    Boolean condition = true;

                    if (fcc.expression() != null) {
                        Object value = visitExpression(fcc.expression());

                        if (value instanceof ValueRef) {
                            condition = (Boolean) ((ValueRef) value).getValue();
                        } else {
                            condition = (Boolean) value;
                        }
                    }

                    if (condition) {
                        obj = visitStatement(ctx.statement(0));

                        if (obj instanceof BreakObject) {
                            obj = null;
                            break;
                        } else if (obj instanceof ReturnObject) {
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
            obj = visitBlock(ctx.blockLabel);
        } else if (ctx.BREAK() != null) {
            obj = BreakObject.instance();
        } else if (ctx.RETURN() != null) {
            if (ctx.expression() != null) {
                obj = visitExpression(ctx.expression());

                if (obj instanceof ValueRef) {
                    obj = ((ValueRef) obj).getValue();
                }

                if (obj instanceof FunctionObject) {
                    // 把闭包涉及的环境变量都打包带走
                    FunctionObject functionObject = (FunctionObject) obj;
                    getClosureValues(functionObject.function, functionObject);
                } else if (obj instanceof KlassObject) {
                    // 如果返回的是一个对象，那么检查它的所有属性里有没有是闭包的
                    // 如果属性仍然是一个对象，可能就要向下递归查找了
                    KlassObject klassObject = (KlassObject) obj;
                    getClosureValues(klassObject);
                }
            }

            obj = new ReturnObject(obj);
        }

        return obj;
    }

    @Override
    public Object visitType(ScriptParser.TypeContext ctx) {
        return visitPrimitiveType(ctx.primitiveType());
    }

    @Override
    public Object visitVariableDeclarator(ScriptParser.VariableDeclaratorContext ctx) {
        Object obj = null;
        ValueRef valueRef = (ValueRef) visitVariableDeclaratorId(ctx.variableDeclaratorId());

        if (ctx.variableInitializer() != null) {
            obj = visitVariableInitializer(ctx.variableInitializer());

            if (obj instanceof ValueRef) {
                obj = ((ValueRef) obj).getValue();
            }
            valueRef.setValue(obj);
        }

        return obj;
    }

    @Override
    public Object visitVariableDeclaratorId(ScriptParser.VariableDeclaratorIdContext ctx) {
        Object obj = null;
        Symbol symbol = tree.nodeToSymbol.get(ctx);
        obj = getValueRef((Variable) symbol);
        return obj;
    }

    @Override
    public Object visitVariableDeclarators(ScriptParser.VariableDeclaratorsContext ctx) {
        Object obj = null;

        for (ScriptParser.VariableDeclaratorContext child : ctx.variableDeclarator()) {
            obj = visitVariableDeclarator(child);
        }

        return obj;
    }

    @Override
    public Object visitVariableInitializer(ScriptParser.VariableInitializerContext ctx) {
        Object obj = null;

        if (ctx.expression() != null) {
            obj = visitExpression(ctx.expression());
        }

        return obj;
    }

    @Override
    public Object visitBlockStatements(ScriptParser.BlockStatementsContext ctx) {
        Object obj = null;

        for (ScriptParser.BlockStatementContext child : ctx.blockStatement()) {
            obj = visitBlockStatement(child);

            // 如果返回的是 break，那么不执行下面的语句
            if (obj instanceof BreakObject) {
                break;
            } else if (obj instanceof ReturnObject) {
                break;
            }
        }

        return obj;
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

        Object obj = null;
        String functionName = ctx.IDENTIFIER().getText();
        Symbol symbol = tree.nodeToSymbol.get(ctx);

        if (symbol instanceof DefaultConstructor) {
            return createAndInitClassObject(((DefaultConstructor) symbol).klass());
        } else if (functionName.equals("println")) {
            println(ctx);
            return obj;
        }

        // 在上下文中查找出函数，并根据需要创建 FunctionObject
        FunctionObject functionObject = getFunctionObject(ctx);
        Function function = functionObject.function;

        if (function.isConstructor()) {
            Klass klass = (Klass) function.enclosingScope;
            KlassObject newObj = createAndInitClassObject(klass);
            methodCall(newObj, ctx, false);
            return newObj;
        }

        List<Object> paramValues = calcParamValues(ctx);

        if (traceFunctionCall) {
            System.out.println("\n>>FunctionCall : " + ctx.getText());
        }

        obj = functionCall(functionObject, paramValues);
        return obj;
    }

    @Override
    public Object visitFunctionDeclaration(ScriptParser.FunctionDeclarationContext ctx) {
        return visitFunctionBody(ctx.functionBody());
    }

    @Override
    public Object visitFunctionBody(ScriptParser.FunctionBodyContext ctx) {
        Object obj = null;

        if (ctx.block() != null) {
            obj = visitBlock(ctx.block());
        }
        return obj;
    }

    @Override
    public Object visitClassBody(ScriptParser.ClassBodyContext ctx) {
        Object obj = null;

        for (ScriptParser.ClassBodyDeclarationContext child : ctx.classBodyDeclaration()) {
            obj = visitClassBodyDeclaration(child);
        }

        return obj;
    }

    @Override
    public Object visitClassBodyDeclaration(ScriptParser.ClassBodyDeclarationContext ctx) {
        Object obj = null;

        if (ctx.memberDeclaration() != null) {
            obj = visitMemberDeclaration(ctx.memberDeclaration());
        }
        return obj;
    }

    @Override
    public Object visitMemberDeclaration(ScriptParser.MemberDeclarationContext ctx) {
        Object obj = null;

        if (ctx.fieldDeclaration() != null) {
            obj = visitFieldDeclaration(ctx.fieldDeclaration());
        }
        return obj;
    }

    @Override
    public Object visitFieldDeclaration(ScriptParser.FieldDeclarationContext ctx) {
        Object obj = null;

        if (ctx.variableDeclarators() != null) {
            obj = visitVariableDeclarators(ctx.variableDeclarators());
        }
        return obj;
    }

    private List<Object> calcParamValues(ScriptParser.FunctionCallContext ctx) {
        List<Object> paramValues = new LinkedList<>();

        if (ctx.expressionList() != null) {
            for (ScriptParser.ExpressionContext expr : ctx.expressionList().expression()) {
                Object value = visitExpression(expr);

                if (value instanceof ValueRef) {
                    value = ((ValueRef) value).getValue();
                }
                paramValues.add(value);
            }
        }

        return paramValues;
    }

    /**
     * 根据函数调用的上下文，返回一个 FunctionObject
     */
    private FunctionObject getFunctionObject(ScriptParser.FunctionCallContext ctx) {
        if (ctx.IDENTIFIER() == null) {
            return null;
        }

        Function function = null;
        FunctionObject functionObject = null;
        Symbol symbol = tree.nodeToSymbol.get(ctx);

        if (symbol instanceof Variable) {
            Variable variable = (Variable) symbol;
            ValueRef valueRef = getValueRef(variable);
            Object value = valueRef.getValue();

            if (value instanceof FunctionObject) {
                functionObject = (FunctionObject) value;
                function = functionObject.function;
            }
        } else if (symbol instanceof Function) {
            function = (Function) symbol;
        } else {
            String functionName = ctx.IDENTIFIER().getText();
            tree.error("unable to find function or function variable " + functionName, ctx);
            return null;
        }

        if (functionObject == null) {
            functionObject = new FunctionObject(function);
        }

        return functionObject;
    }

    /**
     * 执行一个函数的方法体。需要先设置参数值，然后再执行代码
     */
    private Object functionCall(FunctionObject functionObject, List<Object> paramValues) {
        Object obj = null;

        // 添加函数的栈桢
        StackFrame frame = new StackFrame(functionObject);
        pushStack(frame);
        ScriptParser.FunctionDeclarationContext fdc = (ScriptParser.FunctionDeclarationContext) functionObject.function.ctx;

        if (fdc.formalParameters().formalParameterList() != null) {
            for (int i = 0; i < fdc.formalParameters().formalParameterList().formalParameter().size(); i++) {
                ScriptParser.FormalParameterContext param = fdc.formalParameters().formalParameterList().formalParameter(i);
                ValueRef valueRef = (ValueRef) visitVariableDeclaratorId(param.variableDeclaratorId());
                valueRef.setValue(paramValues.get(i));
            }
        }

        // 调用函数（方法）体
        obj = visitFunctionDeclaration(fdc);
        popStack();

        if (obj instanceof ReturnObject) {
            obj = ((ReturnObject) obj).value;
        }

        return obj;
    }

    /**
     * 对象方法调用
     */
    private Object methodCall(KlassObject klassObject, ScriptParser.FunctionCallContext ctx, boolean isSuper) {
        Object obj = null;
        StackFrame frame = new StackFrame(klassObject);
        pushStack(frame);
        FunctionObject functionObject = getFunctionObject(ctx);
        popStack();
        Function function = functionObject.function;

        // 对普通的类方法，需要在运行时动态绑定
        Klass klass = klassObject.klass;

        if (!function.isConstructor() && !isSuper) {
            // 从当前类逐级向上查找，找到正确的方法定义
            Function overrideFunction = klass.getFunction(function.name, function.getParamTypes());

            if ((overrideFunction != null) && (overrideFunction != function)) {
                function = overrideFunction;
                functionObject.function = function;
            }
        }

        List<Object> paramValues = calcParamValues(ctx);
        pushStack(frame);
        obj = functionCall(functionObject, paramValues);
        popStack();
        return obj;
    }

    private void thisConstructor(ScriptParser.FunctionCallContext ctx) {
        Symbol symbol = tree.nodeToSymbol.get(ctx);

        if (symbol instanceof DefaultConstructor) {
            return;
        } else if (symbol instanceof Function) {
            Function function = (Function) symbol;
            FunctionObject functionObject = new FunctionObject(function);
            List<Object> paramValues = calcParamValues(ctx);
            functionCall(functionObject, paramValues);
        }
    }

    /**
     * 为闭包获取环境变量的值
     */
    private void getClosureValues(Function function, ActivationRecord record) {
        if (function.closureVars != null) {
            for (Variable variable : function.closureVars) {
                // 现在还可以从栈里取，退出函数以后就不行了
                ValueRef valueRef = getValueRef(variable);
                Object value = valueRef.getValue();
                record.setValue(variable, value);
            }
        }
    }

    /**
     * 为从函数中返回的对象设置闭包值。因为多个函数型属性可能共享值，所以要打包到 ClassObject 中，而不是 functionObject 中
     */
    private void getClosureValues(KlassObject klassObject) {
        ActivationRecord record = new ActivationRecord();

        for (Variable variable : klassObject.members.keySet()) {
            if (variable.type instanceof FunctionType) {
                Object obj = klassObject.getValue(variable);

                if (obj != null) {
                    FunctionObject functionObject = (FunctionObject) obj;
                    getClosureValues(functionObject.function, record);
                }
            }
        }

        klassObject.members.putAll(record.members);
    }

    private KlassObject createAndInitClassObject(Klass klass) {
        KlassObject obj = new KlassObject();
        obj.klass = klass;
        Stack<Klass> chain = new Stack<>();
        chain.push(klass);

        while (klass.getParent() != null) {
            chain.push(klass.getParent());
            klass = klass.getParent();
        }

        StackFrame frame = new StackFrame(obj);
        pushStack(frame);

        while (!chain.isEmpty()) {
            Klass k = chain.pop();
            defaultObjectInit(k, obj);
        }

        popStack();
        return obj;
    }

    private void defaultObjectInit(Klass klass, KlassObject obj) {
        for (Symbol symbol : klass.symbols) {
            if (symbol instanceof Variable) {
                obj.members.put((Variable) symbol, null);
            }
        }

        ScriptParser.ClassBodyContext ctx = ((ScriptParser.ClassDeclarationContext) klass.ctx).classBody();
        visitClassBody(ctx);

        // 这里还没干完活。还需要调用显式声明的构造方法
    }

    private Object add(Object obj1, Object obj2, Type targetType) {
        Object obj = null;

        if (targetType == PrimitiveType.String) {
            obj = String.valueOf(obj1) + String.valueOf(obj2);
        } else if (targetType == PrimitiveType.Integer) {
            obj = ((Number) obj1).intValue() + ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            obj = ((Number) obj1).floatValue() + ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            obj = ((Number) obj1).longValue() + ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            obj = ((Number) obj1).doubleValue() + ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            obj = ((Number) obj1).shortValue() + ((Number) obj2).shortValue();
        } else {
            System.out.println("unsupported add operation");
        }

        return obj;
    }

    private Object sub(Object obj1, Object obj2, Type targetType) {
        Object obj = null;

        if (targetType == PrimitiveType.Integer) {
            obj = ((Number) obj1).intValue() - ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            obj = ((Number) obj1).floatValue() - ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            obj = ((Number) obj1).longValue() - ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            obj = ((Number) obj1).doubleValue() - ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            obj = ((Number) obj1).shortValue() - ((Number) obj2).shortValue();
        }

        return obj;
    }

    private Object mul(Object obj1, Object obj2, Type targetType) {
        Object obj = null;

        if (targetType == PrimitiveType.Integer) {
            obj = ((Number) obj1).intValue() * ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            obj = ((Number) obj1).floatValue() * ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            obj = ((Number) obj1).longValue() * ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            obj = ((Number) obj1).doubleValue() * ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            obj = ((Number) obj1).shortValue() * ((Number) obj2).shortValue();
        }

        return obj;
    }

    private Object div(Object obj1, Object obj2, Type targetType) {
        Object obj = null;

        if (targetType == PrimitiveType.Integer) {
            obj = ((Number) obj1).intValue() / ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            obj = ((Number) obj1).floatValue() / ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            obj = ((Number) obj1).longValue() / ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            obj = ((Number) obj1).doubleValue() / ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            obj = ((Number) obj1).shortValue() / ((Number) obj2).shortValue();
        }

        return obj;
    }

    private Boolean eq(Object obj1, Object obj2, Type targetType) {
        Boolean obj = null;

        if (targetType == PrimitiveType.Integer) {
            obj = ((Number) obj1).intValue() == ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            obj = ((Number) obj1).floatValue() == ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            obj = ((Number) obj1).longValue() == ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            obj = ((Number) obj1).doubleValue() == ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            obj = ((Number) obj1).shortValue() == ((Number) obj2).shortValue();
        } else {
            // 对于对象实例、函数，直接比较对象引用
            obj = obj1 == obj2;
        }

        return obj;
    }

    private Object ge(Object obj1, Object obj2, Type targetType) {
        Object obj = null;

        if (targetType == PrimitiveType.Integer) {
            obj = ((Number) obj1).intValue() >= ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            obj = ((Number) obj1).floatValue() >= ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            obj = ((Number) obj1).longValue() >= ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            obj = ((Number) obj1).doubleValue() >= ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            obj = ((Number) obj1).shortValue() >= ((Number) obj2).shortValue();
        }

        return obj;
    }

    private Object gt(Object obj1, Object obj2, Type targetType) {
        Object obj = null;

        if (targetType == PrimitiveType.Integer) {
            obj = ((Number) obj1).intValue() > ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            obj = ((Number) obj1).floatValue() > ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            obj = ((Number) obj1).longValue() > ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            obj = ((Number) obj1).doubleValue() > ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            obj = ((Number) obj1).shortValue() > ((Number) obj2).shortValue();
        }

        return obj;
    }

    private Object le(Object obj1, Object obj2, Type targetType) {
        Object obj = null;

        if (targetType == PrimitiveType.Integer) {
            obj = ((Number) obj1).intValue() <= ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            obj = ((Number) obj1).floatValue() <= ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            obj = ((Number) obj1).longValue() <= ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            obj = ((Number) obj1).doubleValue() <= ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            obj = ((Number) obj1).shortValue() <= ((Number) obj2).shortValue();
        }

        return obj;
    }

    private Object lt(Object obj1, Object obj2, Type targetType) {
        Object obj = null;

        if (targetType == PrimitiveType.Integer) {
            obj = ((Number) obj1).intValue() < ((Number) obj2).intValue();
        } else if (targetType == PrimitiveType.Float) {
            obj = ((Number) obj1).floatValue() < ((Number) obj2).floatValue();
        } else if (targetType == PrimitiveType.Long) {
            obj = ((Number) obj1).longValue() < ((Number) obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            obj = ((Number) obj1).doubleValue() < ((Number) obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            obj = ((Number) obj1).shortValue() < ((Number) obj2).shortValue();
        }

        return obj;
    }

    private void pushStack(StackFrame frame) {
        if (!stack.isEmpty()) {
            for (int i = stack.size() - 1; i > 0; i--) {
                StackFrame topFrame = stack.get(i);

                // 如果新加入的栈桢，跟某个已有的栈桢的 enclosingScope 是一样的，那么这俩的 parentFrame 也一样
                if (topFrame.scope.enclosingScope == frame.scope.enclosingScope) {
                    frame.parent = topFrame.parent;
                    break;
                } else if (topFrame.scope == frame.scope.enclosingScope) {
                    frame.parent = topFrame;
                } else if (frame.record instanceof FunctionObject) {
                    FunctionObject object = (FunctionObject) frame.record;

                    // 针对函数是一等公民的情况：函数运行时的作用域，与声明时的作用域会不一致
                    // receiver 机制表示，这个函数是被哪个变量接收了。要按照这个 receiver 的作用域来判断
                    if ((object.receiver != null) && (object.receiver.enclosingScope == topFrame.scope)) {
                        frame.parent = topFrame;
                        break;
                    }
                }
            }

            if (frame.parent == null) {
                frame.parent = stack.peek();
            }
        }

        stack.push(frame);

        if (traceStackFrame) {
            dumpStackFrame();
        }
    }

    private void popStack() {
        stack.pop();
    }

    private void dumpStackFrame(){
        System.out.println("\nStack Frames ----------------");

        for (StackFrame frame : stack) {
            System.out.println(frame);
        }
        System.out.println("-----------------------------\n");
    }

    private void println(ScriptParser.FunctionCallContext ctx) {
        if (ctx.expressionList() != null) {
            Object value = visitExpressionList(ctx.expressionList());

            if (value instanceof ValueRef) {
                value = ((ValueRef) value).getValue();
            }

            System.out.println(value);
        } else {
            System.out.println();
        }
    }
}