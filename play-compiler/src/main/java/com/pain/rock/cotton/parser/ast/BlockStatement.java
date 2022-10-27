package com.pain.rock.cotton.parser.ast;

public class BlockStatement implements AST {

    @Override
    public boolean accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
