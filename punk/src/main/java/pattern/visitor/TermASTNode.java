package pattern.visitor;

public class TermASTNode implements ASTNode {
    @Override
    public void accept(Visitor visitor) {

        if (visitor instanceof TermVisitor) {
            ((TermVisitor) visitor).visitTerm(this);
        } else {
            visitor.visit(this);
        }
    }
}
