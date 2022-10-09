package pattern.visitor;

public interface ASTNode {
    void accept(Visitor visitor);
}
