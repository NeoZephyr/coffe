package com.pain.rock.easy.parser.stage;

public interface Tree {

    Tree getParent();

    void setParent(ParseContext context);

    Tree getChild(int i);

    int getChildCount();

    Object getPayload();

    String toStringTree();

    // <T> T accept(TreeVisitor<? extends T> var1);
}
