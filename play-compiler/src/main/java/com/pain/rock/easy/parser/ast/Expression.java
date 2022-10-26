package com.pain.rock.easy.parser.ast;

public class Expression implements AST {
    @Override
    public boolean accept(ASTVisitor visitor) {
        return false;
    }
}
