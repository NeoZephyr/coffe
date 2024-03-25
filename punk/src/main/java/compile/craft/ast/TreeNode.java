package compile.craft.ast;

import compile.craft.frontend.Token;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract class TreeNode {
    private List<TreeNode> children = new ArrayList<>();

    @Getter
    private TreeNode parent;
    String type;

    private String text;

    Token start;
    Token stop;

    public TreeNode getChild(int i) {
        if (children == null || i < 0 || i >= children.size()) {
            return null;
        }

        return children.get(i);
    }

    public <T extends TreeNode> T getChild(Class<? extends T> clazz, int i) {
        if (children == null || i < 0 || i >= children.size()) {
            return null;
        }

        int j = -1;

        for (TreeNode o : children) {
            if (clazz.isInstance(o)) {
                j++;

                if (j == i) {
                    return clazz.cast(o);
                }
            }
        }
        return null;
    }

    public int getChildCount() {
        if (children == null) {
            return 0;
        }

        return children.size();
    }

    public String getText() {
        if (getChildCount() == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < getChildCount(); i++) {
            builder.append(getChild(i).getText());
        }

        return builder.toString();
    }

    public abstract <T> T accept(TreeVisitor<? extends T> visitor);
}
