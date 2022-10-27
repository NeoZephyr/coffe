package com.pain.rock.easy.parser;

import java.util.List;

public interface ASTNode {

    ASTNode getParent();

    List<ASTNode> getChildren();

    ASTNodeType getType();

    String getText();
}
