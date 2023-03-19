package compile.craft.ast;

public class Expression implements AST {
    @Override
    public boolean accept(ASTVisitor visitor) {
        return false;
    }
}
