package compile.craft.ast;

public class Block implements ASTNode {

    @Override
    public boolean accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
