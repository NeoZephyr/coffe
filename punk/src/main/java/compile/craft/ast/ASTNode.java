package compile.craft.ast;

public interface ASTNode {
    boolean accept(ASTVisitor visitor);
}
