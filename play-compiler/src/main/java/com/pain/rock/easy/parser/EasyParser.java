package com.pain.rock.easy.parser;

import com.pain.rock.easy.lexer.EasyLexer;
import com.pain.rock.easy.lexer.Token;
import com.pain.rock.easy.lexer.TokenReader;
import com.pain.rock.easy.lexer.TokenType;
import com.pain.rock.easy.parser.ast.*;
import com.pain.rock.easy.parser.stage.ASTNode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class EasyParser {

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
    public ASTNode parse(String script) throws Exception {
        if (StringUtils.isBlank(script)) {
            return null;
        }

        EasyLexer lexer = new EasyLexer();
        TokenReader tokenReader = lexer.tokenize(script);
        return program1(tokenReader);
    }

    public Program parse0(String script) throws Exception {
        if (StringUtils.isBlank(script)) {
            return null;
        }

        EasyLexer lexer = new EasyLexer();
        TokenReader tokenReader = lexer.tokenize(script);
        return program(tokenReader);
    }

    public void dump(ASTNode node, String indent) {
        if (node == null) {
            return;
        }

        System.out.printf("%s%s %s\n", indent, node.getType(), node.getText());

        for (ASTNode child : node.getChildren()) {
            dump(child, indent + "\t");
        }
    }

    private ASTNode program1(TokenReader reader) throws Exception {
        EasyASTNode node = new EasyASTNode(ASTNodeType.Program, "program");

        while (reader.peek() != null) {
            EasyASTNode child = intDeclaration(reader);

            if (child == null) {
                child = expressionStmt(reader);
            }

            if (child == null) {
                child = assignmentStmt(reader);
            }

            if (child != null) {
                node.addChild(child);
            } else {
                throw new Exception("unknown statement");
            }
        }

        return node;
    }

    /**
     * prog : blockStatements
     *      ;
     */
    private Program program(TokenReader reader) {
        List<BlockStatement> blockStatements = blockStatements(reader);
        return new Program(blockStatements);
    }

    /**
     * blockStatements : blockStatement*
     *                 ;
     */
    private List<BlockStatement> blockStatements(TokenReader reader) {
        List<BlockStatement> blockStatements = new ArrayList<>();

        while (true) {
            BlockStatement blockStatement = blockStatement(reader);

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
    private BlockStatement blockStatement(TokenReader reader) {
        return null;
    }

    /**
     * variableDeclarators : typeType variableDeclarator (',' variableDeclarator)*
     *                     ;
     */
    private VariableDeclarators variableDeclarators(TokenReader reader) {
        Type type = type(reader);
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
    private Statement statement(TokenReader reader) {
        return null;
    }

    /**
     * typeType : (classOrInterfaceType | functionType | primitiveType) ('[' ']')*
     *          ;
     */
    private Type type(TokenReader reader) {
        return null;
    }

    /**
     * variableDeclarator : variableDeclaratorId ('=' variableInitializer)?
     *                    ;
     */
    private VariableDeclarator variableDeclarator(TokenReader reader) {
        return null;
    }

    /**
     * variableDeclaratorId : IDENTIFIER ('[' ']')*
     *                      ;
     */
    private VariableDeclaratorId variableDeclaratorId(TokenReader reader) {
        return null;
    }

    /**
     * variableInitializer : arrayInitializer
     *                     | expression
     *                     ;
     */
    private VariableInitializer variableInitializer(TokenReader reader) {
        return null;
    }

    /**
     * arrayInitializer : '{' (variableInitializer (',' variableInitializer)* (',')? )? '}'
     *                  ;
     */
    private ArrayInitializer arrayInitializer(TokenReader reader) {
        return null;
    }

    /**
     * expression : primary
     *            | expression postfix=('++' | '--')
     *            | prefix=('+'|'-'|'++'|'--') expression
     *            ;
     */
    private Expression expression(TokenReader reader) {
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
    private Primary primary(TokenReader reader) {
        return null;
    }

    private EasyASTNode intDeclaration(TokenReader reader) throws Exception {
        Token token = reader.peek();
        EasyASTNode node = null;

        if (token != null && token.getType() == TokenType.Int) {
            reader.read();
            token = reader.read();

            if (token != null && token.getType() == TokenType.Identifier) {
                node = new EasyASTNode(ASTNodeType.IntDeclaration, token.getText());
            } else {
                throw new Exception("invalid intDeclaration, except variable name");
            }

            token = reader.peek();

            if (token != null && token.getType() == TokenType.Assignment) {
                reader.read();
                EasyASTNode child = additive(reader);

                if (child != null) {
                    node.addChild(child);
                } else {
                    throw new Exception("invalid intDeclaration, expect initialize expression");
                }
            }

            token = reader.read();

            if (token == null || token.getType() != TokenType.SemiColon) {
                throw new Exception("invalid intDeclaration, expect semicolon");
            }
        }

        return node;
    }

    private EasyASTNode expressionStmt(TokenReader reader) throws Exception {
        int position = reader.getPosition();
        EasyASTNode node = additive(reader);

        if (node != null) {
            Token token = reader.read();

            if (token != null && token.getType() == TokenType.SemiColon) {
                return node;
            } else {
                reader.setPosition(position);
                return null;
            }
        }

        return null;
    }

    // TODO 吐出来
    private EasyASTNode assignmentStmt(TokenReader reader) throws Exception {
        Token token = reader.peek();

        if (token == null || token.getType() != TokenType.Identifier) {
            return null;
        }

        reader.read();
        EasyASTNode child = new EasyASTNode(ASTNodeType.Identifier, token.getText());
        token = reader.peek();

        if (token != null && token.getType() == TokenType.Assignment) {
            reader.read();
            EasyASTNode root = new EasyASTNode(ASTNodeType.AssignmentStmt, token.getText());
            root.addChild(child);
            child = additive(reader);

            if (child == null) {
                throw new Exception("invalid assignmentStmt, expect right part");
            }

            token = reader.read();

            if (token == null || token.getType() != TokenType.SemiColon) {
                throw new Exception("invalid assignmentStmt, expect semi colon");
            }

            root.addChild(child);
            return root;
        } else {
            reader.unread();
        }

        return null;
    }

    private EasyASTNode additive(TokenReader reader) throws Exception {
        EasyASTNode child1 = multiplicative(reader);
        EasyASTNode root = child1;

        if (root == null) {
            return null;
        }

        while (true) {
            Token token = reader.peek();

            if (token != null && (token.getType() == TokenType.Plus || token.getType() == TokenType.Minus)) {
                reader.read();
                EasyASTNode child2 = multiplicative(reader);

                if (child2 != null) {
                    root = new EasyASTNode(ASTNodeType.Additive, token.getText());
                    root.addChild(child1);
                    root.addChild(child2);
                    child1 = root;
                } else {
                    throw new Exception("invalid additive, expect right part");
                }
            } else {
                break;
            }
        }

        return root;
    }

    private EasyASTNode multiplicative(TokenReader reader) throws Exception {
        EasyASTNode child1 = primary1(reader);
        EasyASTNode root = child1;

        if (root == null) {
            return null;
        }

        while (true) {
            Token token = reader.peek();

            if (token != null && (token.getType() == TokenType.Star || token.getType() == TokenType.Slash)) {
                reader.read();
                EasyASTNode child2 = primary1(reader);

                if (child2 != null) {
                    root = new EasyASTNode(ASTNodeType.Multiplicative, token.getText());
                    root.addChild(child1);
                    root.addChild(child2);
                    child1 = root;
                } else {
                    throw new Exception("invalid multiplicative, expect right part");
                }
            } else {
                break;
            }
        }

        return root;
    }

    // Primary -> IntLiteral | Identifier | '(' Additive ')' | ('+' | '-') Primary
    private EasyASTNode primary1(TokenReader reader) throws Exception {
        Token token = reader.peek();

        if (token == null) {
            return null;
        }

        if (token.getType() == TokenType.IntLiteral) {
            reader.read();
            return new EasyASTNode(ASTNodeType.IntLiteral, token.getText());
        }

        if (token.getType() == TokenType.Identifier) {
            reader.read();
            return new EasyASTNode(ASTNodeType.Identifier, token.getText());
        }

        if ((token.getType() == TokenType.Plus) || (token.getType() == TokenType.Minus)) {
            reader.read();
            EasyASTNode root = new EasyASTNode(ASTNodeType.Unary, token.getText());
            EasyASTNode child = primary1(reader);

            if (child == null) {
                throw new Exception("invalid primary, expect primary after unary operator");
            }

            root.addChild(child);
            return root;
        }

        if (token.getType() == TokenType.LeftParenthesis) {
            reader.read();
            EasyASTNode node = additive(reader);

            if (node == null) {
                throw new Exception("invalid primary, expect additive in parenthesis");
            }

            token = reader.read();

            if (token != null && token.getType() == TokenType.RightParenthesis) {
                return node;
            } else {
                throw new Exception("invalid primary, expect right parenthesis");
            }
        }

        throw new Exception("invalid primary: " + token.getText());
    }

    /**
     * add -> mul | mul + add | mul - add
     */
    private EasyASTNode additiveRecursive(TokenReader reader) throws Exception {
        EasyASTNode child1 = multiplicativeRecursive(reader);
        EasyASTNode root = child1;
        Token token = reader.peek();

        if (token != null && child1 != null) {
            if (token.getType() == TokenType.Plus || token.getType() == TokenType.Minus) {
                token = reader.read();
                EasyASTNode child2 = additiveRecursive(reader);

                if (child2 != null) {
                    root = new EasyASTNode(ASTNodeType.Additive, token.getText());
                    root.addChild(child1);
                    root.addChild(child2);
                } else {
                    throw new Exception("invalid, expect right part");
                }
            }
        }

        return root;
    }

    /**
     * mul -> pri | pri * mul | pri / mul
     */
    private EasyASTNode multiplicativeRecursive(TokenReader reader) throws Exception {
        EasyASTNode child1 = primary1(reader);
        EasyASTNode root = child1;
        Token token = reader.peek();

        if (child1 != null && token != null) {
            if (token.getType() == TokenType.Star || token.getType() == TokenType.Slash) {
                token = reader.read();
                EasyASTNode child2 = multiplicativeRecursive(reader);

                if (child2 != null) {
                    root = new EasyASTNode(ASTNodeType.Multiplicative, token.getText());
                    root.addChild(child1);
                    root.addChild(child2);
                } else {
                    throw new Exception("invalid, expect right part");
                }
            }
        }

        return root;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    private class EasyASTNode implements ASTNode {
        private ASTNodeType type;
        private String text;
        private ASTNode parent;
        private List<ASTNode> children;

        EasyASTNode(ASTNodeType type, String text) {
            this.type = type;
            this.text = text;
            this.children = new ArrayList<>();
        }

        @Override
        public ASTNode getParent() {
            return parent;
        }

        @Override
        public List<ASTNode> getChildren() {
            return children;
        }

        @Override
        public ASTNodeType getType() {
            return type;
        }

        @Override
        public String getText() {
            return text;
        }

        public void addChild(EasyASTNode node) {
            node.parent = this;
            children.add(node);
        }
    }
}
