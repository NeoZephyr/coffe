package compile.antlr.script;

import compile.antlr.script.symbol.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;

import java.util.HashSet;
import java.util.Set;

public class ClosureAnalyzer {

    private AnnotatedTree tree;

    public ClosureAnalyzer(AnnotatedTree tree) {
        this.tree = tree;
    }

    /**
     * 只做标准函数的分析，不做类的方法的分析
     */
    public void analyzeClosures() {
        for (Type type : tree.types) {
            if (type instanceof Function && !((Function) type).isMethod()) {
                Set set = calcClosureVariables((Function) type);

                if (set.size() > 0) {
                    ((Function) type).closureVariables = set;
                }
            }
        }
    }

    private Set<Variable> calcClosureVariables(Function function) {
        Set<Variable> refer = variablesReferByScope(function);
        Set<Variable> declare = variablesDeclareUnderScope(function);
        refer.removeAll(declare);
        return refer;
    }

    private Set<Variable> variablesReferByScope(Scope scope) {
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

    private boolean isAncestor(RuleContext node1, RuleContext node2) {
        if (node2.parent == null) {
            return false;
        } else if (node2.parent == node1) {
            return true;
        } else {
            return isAncestor(node1, node2.parent);
        }
    }

    private Set<Variable> variablesDeclareUnderScope(Scope scope) {
        Set<Variable> set = new HashSet<>();

        for (Symbol symbol : scope.getSymbols()) {
            if (symbol instanceof Variable) {
                set.add((Variable) symbol);
            } else if (symbol instanceof Scope) {
                set.addAll(variablesDeclareUnderScope((Scope) symbol));
            }
        }

        return set;
    }
}