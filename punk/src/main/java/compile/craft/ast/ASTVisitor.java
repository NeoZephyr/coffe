package compile.craft.ast;

public interface ASTVisitor {
    default boolean visit(Program program) {
        return true;
    }

    default boolean visit(Block block) {
        return true;
    }
}
