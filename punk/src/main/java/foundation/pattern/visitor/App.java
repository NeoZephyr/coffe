package foundation.pattern.visitor;

import java.util.ArrayList;
import java.util.List;

public class App {

    public static void main(String[] args) {
        List<ASTNode> nodes = new ArrayList<>();
        nodes.add(new FactorASTNode());
        nodes.add(new TermASTNode());

        Visitor fv = new FactorVisitor();
        Visitor tv = new TermVisitor();

        for (ASTNode node : nodes) {
            node.accept(fv);
        }

        for (ASTNode node : nodes) {
            node.accept(tv);
        }
    }
}
