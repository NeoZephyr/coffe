package com.pain.rock.cotton.parser.ast;

import com.pain.rock.cotton.parser.ast.AST;
import com.pain.rock.cotton.parser.ast.ASTVisitor;

public class Statement implements AST {

    @Override
    public boolean accept(ASTVisitor visitor) {
        return false;
    }
}
