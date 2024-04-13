package foundation.pattern.visitor;

public class TermVisitor implements Visitor {
    @Override
    public void visit(ASTNode node) {
        System.out.println("visit(ASTNode node)");
    }

    public void visitTerm(TermASTNode node) {
        System.out.println("visitTerm(TermASTNode node)");
    }
}
