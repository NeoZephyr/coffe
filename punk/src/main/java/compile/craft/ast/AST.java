package compile.craft.ast;

public interface AST {
    boolean accept(ASTVisitor visitor);
}
