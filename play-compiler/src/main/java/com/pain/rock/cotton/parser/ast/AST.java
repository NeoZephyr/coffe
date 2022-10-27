package com.pain.rock.cotton.parser.ast;

public interface AST {
    boolean accept(ASTVisitor visitor);
}
