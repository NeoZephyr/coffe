package com.pain.rock.cotton.parser;

import com.pain.rock.cotton.lexer.Token;
import com.pain.rock.cotton.lexer.TokenStream;
import com.pain.rock.cotton.parser.ast.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    // Primary -> IntLiteral | Identifier | '(' Additive ')' | ('+' | '-') Primary
    /**
     * Program -> IntDeclaration | ExpressionStmt | AssignmentStmt
     *
     * IntDeclaration -> 'int' Identifier ('=' Additive) ';'
     * Additive -> Multiplicative (('+' | '-') Multiplicative)*
     * Multiplicative -> Primary (('*' | '/') Primary)*
     * Primary -> IntLiteral | Identifier | '(' Additive ')'
     *
     * ExpressionStmt -> Additive ';'
     *
     * AssignmentStmt -> Identifier '=' Additive ';'
     *
     */

    public TokenStream input;

    public Parser(TokenStream input) {
        this.input = input;
    }

    /**
     * prog : blockStatements ;
     */
    private Program program() {
        List<BlockStatement> blockStatements = blockStatements();
        return new Program(blockStatements);
    }

    /**
     * blockStatements : blockStatement* ;
     */
    private List<BlockStatement> blockStatements() {
        List<BlockStatement> blockStatements = new ArrayList<>();
        Token token = input.peek(1);

        while (true) {
            BlockStatement blockStatement = blockStatement();

            if (blockStatement == null) {
                break;
            }

            blockStatements.add(blockStatement);
        }
        return blockStatements;
    }

    /**
     * blockStatement : variableDeclarators ';'
     *                | statement
     *                | functionDeclaration
     *                | classDeclaration
     *                ;
     */
    private BlockStatement blockStatement() {
        return null;
    }

    /**
     * variableDeclarators : typeType variableDeclarator (',' variableDeclarator)* ;
     */
    private VariableDeclarators variableDeclarators() {
        Type type = type();
        List<VariableDeclarator> variableDeclarators = new ArrayList<>();
        return new VariableDeclarators(type, variableDeclarators);
    }

    /**
     * statement
     *     : blockLabel=block
     *     | RETURN expression? ';'
     *     | SEMI
     *     | statementExpression=expression ';'
     *     ;
     */
    private Statement statement() {
        return null;
    }

    /**
     * typeType : (classOrInterfaceType | functionType | primitiveType) ('[' ']')* ;
     */
    private Type type() {
        return null;
    }

    /**
     * variableDeclarator : variableDeclaratorId ('=' variableInitializer)?
     *                    ;
     */
    private VariableDeclarator variableDeclarator() {
        return null;
    }

    /**
     * variableDeclaratorId : IDENTIFIER ('[' ']')*
     *                      ;
     */
    private VariableDeclaratorId variableDeclaratorId() {
        return null;
    }

    /**
     * variableInitializer : arrayInitializer
     *                     | expression
     *                     ;
     */
    private VariableInitializer variableInitializer() {
        return null;
    }

    /**
     * arrayInitializer : '{' (variableInitializer (',' variableInitializer)* (',')? )? '}'
     *                  ;
     */
    private ArrayInitializer arrayInitializer() {
        return null;
    }

    /**
     * expression : primary
     *            | expression postfix=('++' | '--')
     *            | prefix=('+'|'-'|'++'|'--') expression
     *            ;
     */
    private Expression expression() {
        return null;
    }

    /**
     * primary : '(' expression ')'
     *         | THIS
     *         | SUPER
     *         | literal
     *         | IDENTIFIER
     *         ;
     */
    private Primary primary() {
        return null;
    }
}
