package compile.antlr.script.analyzer;

import compile.antlr.script.symbol.Function;
import compile.antlr.script.symbol.Scope;
import compile.antlr.script.symbol.Symbol;
import compile.antlr.script.symbol.Variable;
import compile.antlr.script.types.Type;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;

import java.util.HashSet;
import java.util.Set;

public class ClosureAnalyzer {

    public AnnotatedTree tree = null;

    public  ClosureAnalyzer(AnnotatedTree tree){
        this.tree = tree;
    }

    public void analyze() {
        for (Type type : tree.types) {
            if ((type instanceof Function) && !(((Function) type).isMethod())) {
                Set<Variable> set = calcClosureVars((Function) type);

                if (!set.isEmpty()) {
                    ((Function) type).closureVars = set;
                }
            }
        }
    }

    /**
     * 为某个函数计算闭包变量，也就是它所引用的外部环境变量
     * 算法：计算所有的变量引用，去掉内部声明的变量，剩下的就是外部的
     */
    private Set<Variable> calcClosureVars(Function function) {
        Set<Variable> refer = varsReferByScope(function);
        Set<Variable> declare = varsDeclareUnderScope(function);
        refer.removeAll(declare);
        return refer;
    }

    /**
     * 被一个 Scope（包括下级 Scope）内部的代码所引用的所有变量的集合
     */
    private Set<Variable> varsReferByScope(Scope scope) {
        Set<Variable> set = new HashSet<>();
        ParserRuleContext ctx = scope.ctx;

        for (ParserRuleContext node : tree.nodeToSymbol.keySet()) {
            Symbol symbol = tree.nodeToSymbol.get(node);

            if (symbol instanceof Variable && isAncestor(ctx, node)) {
                set.add((Variable) symbol);
            }
        }

        return set;
    }

    /**
     * 在一个 Scope（及）下级 Scope 中声明的所有变量的集合
     */
    private Set<Variable> varsDeclareUnderScope(Scope scope) {
        Set<Variable> set = new HashSet<>();

        for (Symbol symbol : scope.symbols) {
            if (symbol instanceof Variable) {
                set.add((Variable) symbol);
            } else if (symbol instanceof Scope) {
                set.addAll(varsDeclareUnderScope((Scope) symbol));
            }
        }

        return set;
    }

    private boolean isAncestor(RuleContext node1, RuleContext node2) {
        if (node2.parent == null) {
            return false;
        } else if (node2.parent == node1) {
            return true;
        } else {
            return isAncestor(node1, node2.parent);
        }
    }
}