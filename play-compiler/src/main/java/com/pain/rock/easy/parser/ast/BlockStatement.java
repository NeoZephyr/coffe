package com.pain.rock.easy.parser.ast;

public class BlockStatement implements AST {

    @Override
    public boolean accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
