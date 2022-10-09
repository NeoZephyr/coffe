package pattern.visitor;

public class FactorVisitor implements Visitor {
    @Override
    public void visit(ASTNode node) {
        System.out.println("visit(ASTNode node)");
    }

    public void visitFactor(FactorASTNode node) {
        System.out.println("visitFactor(FactorASTNode node)");
    }
}
