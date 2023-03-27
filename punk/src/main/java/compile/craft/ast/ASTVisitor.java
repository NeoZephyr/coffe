package compile.craft.ast;

public interface ASTVisitor {
    default boolean visit(Program program) {
        return true;
    }

    default boolean visit(BlockStatement blockStatement) {
        return true;
    }

    default boolean visit(VariableDeclarators variableDeclarators) {
        return true;
    }
}