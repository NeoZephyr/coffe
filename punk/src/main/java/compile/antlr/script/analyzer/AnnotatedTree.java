package compile.antlr.script.analyzer;

import compile.antlr.script.ScriptParser;
import compile.antlr.script.symbol.*;
import compile.antlr.script.types.Type;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 语义分析的结果
 * 1. 类型信息，包括基本类型和用户自定义类型
 * 2. 变量和函数调用的消解
 * 3. 作用域 Scope，在 Scope 中包含了该作用域的所有符号
 */
@Slf4j
public class AnnotatedTree {

    public ParseTree ast = null;

    // 解析出来的所有类型
    public List<Type> types = new LinkedList<>();

    // AST 节点对应的 Symbol
    public Map<ParserRuleContext, Symbol> nodeToSymbol = new HashMap<>();

    // AST 节点对应的 Scope
    public Map<ParserRuleContext, Scope> nodeToScope = new HashMap<>();

    public Map<ParserRuleContext, Type> nodeToType = new HashMap<>();

    // 命名空间
    protected Namespace namespace = null;

    // 语义分析过程中生成的信息，包括普通信息、警告和错误
    public List<CompilationLog> logs = new LinkedList<>();

    // 在构造函数里，引用的 this()
    protected Map<Function, Function> thisConstructorRef = new HashMap<>();

    // 在构造函数里，引用的 super()
    protected Map<Function, Function> superConstructorRef = new HashMap<>();

    public AnnotatedTree() {}

    /**
     * 记录编译错误和警告
     */
    protected void append(String message, LogLevel level, ParserRuleContext ctx) {
        CompilationLog logInfo = new CompilationLog();
        logInfo.ctx = ctx;
        logInfo.message = message;
        logInfo.line = ctx.getStart().getLine();
        logInfo.column = ctx.getStart().getStartIndex();
        logInfo.level = level;
        logs.add(logInfo);

        log.info("{}", logInfo);
    }

    public void error(String message, ParserRuleContext ctx) {
        append(message, LogLevel.ERROR, ctx);
    }

    public boolean hasCompilationError() {
        for (CompilationLog log : logs) {
            if (log.level == LogLevel.ERROR) {
                return true;
            }
        }

        return false;
    }

    /**
     * 逐级 Scope 查找
     */
    public Variable lookupVariable(Scope scope, String name) {
        Variable symbol = scope.getVariable(name);

        if ((symbol == null) && (scope.enclosingScope != null)) {
            symbol = lookupVariable(scope.enclosingScope, name);
        }

        return symbol;
    }

    protected Klass lookupKlass(Scope scope, String name) {
        Klass symbol = scope.getKlass(name);

        if ((symbol == null) && (scope.enclosingScope != null)) {
            symbol = lookupKlass(scope.enclosingScope, name);
        }

        return symbol;
    }

    protected Type lookupType(String name) {
        Type type = null;

        for (Type t : types) {
            if (t.getName().equals(name)) {
                type = t;
                break;
            }
        }

        return type;
    }

    protected Function lookupFunction(Scope scope, String name, List<Type> paramTypes) {
        Function function = scope.getFunction(name, paramTypes);

        if ((function == null) && (scope.enclosingScope != null)) {
            function = lookupFunction(scope.enclosingScope, name, paramTypes);
        }

        return function;
    }

    protected Function lookupFunction(Scope scope, String name) {
        Function function = null;

        if (scope instanceof Klass) {
            function = getMethodByName((Klass) scope, name);
        } else {
            function = getFunctionByName(scope, name);
        }

        if ((function == null) && (scope.enclosingScope != null)) {
            function = lookupFunction(scope.enclosingScope, name);
        }

        return function;
    }

    protected Variable lookupFunctionVariable(Scope scope, String name, List<Type> paramTypes) {
        Variable variable = scope.getFuncVariable(name, paramTypes);

        if ((variable == null) && (scope.enclosingScope != null)) {
            variable = lookupFunctionVariable(scope.enclosingScope, name, paramTypes);
        }

        return variable;
    }

    protected Scope enclosingScopeOfNode(ParserRuleContext ctx) {
        Scope scope = null;
        ParserRuleContext parent = ctx.getParent();

        if (parent != null) {
            scope = nodeToScope.get(parent);

            if (scope == null) {
                scope = enclosingScopeOfNode(parent);
            }
        }

        return scope;
    }

    protected Function enclosingFunctionOfNode(RuleContext ctx) {
        if (ctx.parent instanceof ScriptParser.FunctionDeclarationContext) {
            return (Function) nodeToScope.get(ctx.parent);
        } else if (ctx.parent == null) {
            return null;
        } else {
            return enclosingFunctionOfNode(ctx.parent);
        }
    }

    protected Klass enclosingKlassOfNode(RuleContext ctx) {
        if (ctx.parent instanceof ScriptParser.ClassDeclarationContext) {
            return (Klass) nodeToScope.get(ctx.parent);
        } else if (ctx.parent == null) {
            return null;
        } else {
            return enclosingKlassOfNode(ctx.parent);
        }
    }

    public String getScopeTreeText() {
        StringBuilder sb = new StringBuilder();
        scopeToText(sb, namespace, "");
        return sb.toString();
    }

    private Function getMethodByName(Klass klass, String name) {
        Function function = getFunctionByName(klass, name);

        if ((function == null) && (klass.getParent() != null)) {
            function = getMethodByName(klass.getParent(), name);
        }

        return function;
    }

    private Function getFunctionByName(Scope scope, String name) {
        for (Symbol symbol : scope.symbols) {
            if ((symbol instanceof Function) && symbol.name.equals(name)) {
                return (Function) symbol;
            }
        }

        return null;
    }

    private void scopeToText(StringBuilder sb, Scope scope, String indent) {
        sb.append(indent).append(scope).append('\n');

        for (Symbol symbol : scope.symbols) {
            if (symbol instanceof Scope) {
                scopeToText(sb, (Scope) symbol, indent + '\t');
            } else {
                sb.append(indent).append('\t').append(symbol).append('\n');
            }
        }
    }
}