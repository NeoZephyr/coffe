package com.pain.rock.cotton.parser.ast;

import java.util.List;

public class VariableDeclarators implements AST {

    private Type type;
    private List<VariableDeclarator> variableDeclarators;

    public VariableDeclarators() {
    }

    public VariableDeclarators(Type type, List<VariableDeclarator> variableDeclarators) {
        this.type = type;
        this.variableDeclarators = variableDeclarators;
    }

    @Override
    public boolean accept(ASTVisitor visitor) {
        return false;
    }
}
