package foundation.pattern.visitor;

public class FactorASTNode implements ASTNode {
    @Override
    public void accept(Visitor visitor) {

        if (visitor instanceof FactorVisitor) {
            ((FactorVisitor) visitor).visitFactor(this);
        } else {
            visitor.visit(this);
        }
    }
}
