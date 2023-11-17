package compile.antlr.script.symbol;

import compile.antlr.script.scope.Scope;
import org.antlr.v4.runtime.ParserRuleContext;

public abstract class Symbol {

    // 符号的名称
    public String name;

    // 可见性
    private int visibility = 0;

    // 所属作用域
    public Scope enclosingScope = null;

    // Symbol 关联的 AST 节点
    protected ParserRuleContext ctx = null;

}