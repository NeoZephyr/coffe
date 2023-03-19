package compile.antlr.script;

import compile.antlr.script.symbol.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotatedTree {

    public ParseTree ast;
    public Namespace namespace;

    // 解析出来的所有类型，包括类和函数，以后还可以包括数组和枚举。类的方法也作为单独的要素放进去
    protected List<Type> types = new ArrayList<>();

    public Map<ParserRuleContext, Symbol> nodeToSymbol = new HashMap<>();

    public Map<ParserRuleContext, Scope> nodeToScope = new HashMap<>();

    public Map<ParserRuleContext, Type> nodeToType = new HashMap<>();

    protected List<CompilationLog> logs = new ArrayList<>();

    public Map<Function, Function> thisFuncRef = new HashMap<>();

    public Map<Function, Function> superFuncRef = new HashMap<>();

    public AnnotatedTree() {}

    protected void log(String message, int type, ParserRuleContext ctx) {
        CompilationLog log = new CompilationLog();
        log.ctx = ctx;
        log.message = message;
        log.line = ctx.getStart().getLine();
        log.positionInLine = ctx.getStart().getStartIndex();
        log.type = type;
        logs.add(log);

        System.out.println(log);
    }

    public void addType(Type type) {
        types.add(type);
    }

    public void log(String message, ParserRuleContext ctx) {
        this.log(message, CompilationLog.ERROR, ctx);
    }

    public boolean hasCompilationError() {
        for (CompilationLog log : logs) {
            if (log.type == CompilationLog.ERROR) {
                return true;
            }
        }

        return false;
    }

    public Variable lookupVariable(Scope scope, String idName) {
        Variable v = scope.getVariable(idName);

        if (v == null && scope.getEnclosingScope() != null) {
            v = lookupVariable(scope.getEnclosingScope(), idName);
        }

        return v;
    }

    public Klass lookupKlass(Scope scope, String idName) {
        Klass c = scope.getKlass(idName);

        if (c == null && scope.getEnclosingScope() != null) {
            c = lookupKlass(scope.getEnclosingScope(), idName);
        }

        return c;
    }

    public Type lookupType(String idName) {
        for (Type type : types) {
            if (type.getName().equals(idName)) {
                return type;
            }
        }

        return null;
    }

    public Function lookupFunction(Scope scope, String idName, List<Type> paramTypes) {
        Function func = scope.getFunction(idName, paramTypes);

        if (func == null && scope.getEnclosingScope() != null) {
            func = lookupFunction(scope.getEnclosingScope(), idName, paramTypes);
        }

        return func;
    }

    public Function lookupFunction(Scope scope, String name) {
        Function func = null;

        if (scope instanceof Klass) {
            func = getMethodByName((Klass) scope, name);
        } else {
            func = getFunctionByName(scope, name);
        }

        if (func == null && scope.getEnclosingScope() != null) {
            func = lookupFunction(scope.getEnclosingScope(), name);
        }

        return func;
    }

    public Variable lookupFunctionVariable(Scope scope, String idName, List<Type> paramTypes) {
        Variable v = scope.getFunctionVariable(idName, paramTypes);

        if (v == null && scope.getEnclosingScope() != null) {
            v = lookupFunctionVariable(scope.getEnclosingScope(), idName, paramTypes);
        }

        return v;
    }

    private Function getMethodByName(Klass klass, String name) {
        Function func = getFunctionByName(klass, name);

        if (func == null && klass.getParentKlass() != null) {
            func = getMethodByName(klass.getParentKlass(), name);
        }

        return func;
    }

    private Function getFunctionByName(Scope scope, String name) {
        for (Symbol symbol : scope.getSymbols()) {
            if (symbol instanceof Function && symbol.name.equals(name)) {
                return (Function) symbol;
            }
        }

        return null;
    }

    /**
     * 查找节点所在的 Scope
     */
    public Scope scopeOfNode(ParserRuleContext node) {
        ParserRuleContext parent = node.getParent();
        Scope scope = null;

        if (parent != null) {
            scope = nodeToScope.get(parent);

            if (scope == null) {
                scope = scopeOfNode(parent);
            }
        }

        return scope;
    }

    /**
     * 查找包含节点的函数
     */
    public Function functionOfNode(RuleContext ctx) {
        if (ctx.parent instanceof ScriptParser.FunctionDeclarationContext) {
            return (Function) nodeToScope.get(ctx.parent);
        } else if (ctx.parent == null) {
            return null;
        } else {
            return functionOfNode(ctx.parent);
        }
    }

    /**
     * 查找包含节点的类
     */
    public Klass classOfNode(RuleContext ctx) {
        if (ctx.parent instanceof ScriptParser.ClassDeclarationContext) {
            return (Klass) nodeToScope.get(ctx.parent);
        } else if (ctx.parent == null) {
            return null;
        } else {
            return classOfNode(ctx.parent);
        }
    }

    public String getScopeTreeString() {
        StringBuilder sb = new StringBuilder();
        scopeToString(sb, namespace, "");
        return sb.toString();
    }

    private void scopeToString(StringBuilder sb, Scope scope, String indent) {
        sb.append(indent).append(scope).append('\n');

        for (Symbol symbol : scope.getSymbols()) {
            if (symbol instanceof Scope) {
                scopeToString(sb, (Scope) symbol, indent + "\t");
            } else {
                sb.append(indent).append("\t").append(symbol).append("\n");
            }
        }
    }
}
