package com.pain.rock.easy.parser.ast;

public interface AST {
    boolean accept(ASTVisitor visitor);
}
