package compile.craft.ast;

public interface TreeVisitor<T> {
    default T visit(TreeNode tree) {
        return tree.accept(this);
    }
}
