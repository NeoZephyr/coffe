package compile.craft.ast;

import java.util.ArrayList;

public class TreeNode {
    ArrayList<TreeNode> children = new ArrayList<>();
    TreeNode parent;
    String type;
    String text;
}
