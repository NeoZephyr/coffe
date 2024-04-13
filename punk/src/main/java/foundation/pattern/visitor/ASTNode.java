package foundation.pattern.visitor;

public interface ASTNode {
    void accept(Visitor visitor);
}
