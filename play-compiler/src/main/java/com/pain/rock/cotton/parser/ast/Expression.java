package com.pain.rock.cotton.parser.ast;

public class Expression implements AST {
    @Override
    public boolean accept(ASTVisitor visitor) {
        return false;
    }
}
