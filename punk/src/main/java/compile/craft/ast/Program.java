package compile.craft.ast;

public class Program implements ASTNode {

    @Override
    public boolean accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
