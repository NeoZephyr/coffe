package com.pain.rock.easy.parser.stage;

import java.util.ArrayList;
import java.util.List;

public class ParseContext implements Tree {

    private ParseContext parent;
    private List<ParseContext> children;

    @Override
    public Tree getParent() {
        return parent;
    }

    @Override
    public void setParent(ParseContext tree) {
        this.parent = tree;
    }

    @Override
    public ParseContext getChild(int i) {
        if (this.children != null && i >= 0 && i< this.children.size()) {
            return this.children.get(i);
        }

        return null;
    }

    @Override
    public int getChildCount() {
        if (this.children != null) {
            return this.children.size();
        }

        return 0;
    }

    public <T extends ParseContext> T addChild(T child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }

        this.children.add(child);
        child.setParent(this);
        return child;
    }

    @Override
    public Object getPayload() {
        return this;
    }

    @Override
    public String toStringTree() {
        return null;
    }
}
