package compile.easy.parser;

import java.util.List;

public interface ASTNode {

    ASTNode getParent();

    List<ASTNode> getChildren();

    ASTNodeType getType();

    String getText();
}
