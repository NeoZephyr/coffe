package com.pain.rock.easy.parser.stage;

import com.pain.rock.easy.parser.ASTNodeType;

import java.util.List;

public interface ASTNode {

    ASTNode getParent();

    List<ASTNode> getChildren();

    ASTNodeType getType();

    String getText();
}
