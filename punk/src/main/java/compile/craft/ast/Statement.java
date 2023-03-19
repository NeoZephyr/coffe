package compile.craft.ast;

public class Statement implements AST {

    @Override
    public boolean accept(ASTVisitor visitor) {
        return false;
    }
}
