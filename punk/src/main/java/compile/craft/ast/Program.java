package compile.craft.ast;

import java.util.ArrayList;
import java.util.List;

public class Program implements AST {

    private List<BlockStatement> blockStatements;

    public Program() {
        this.blockStatements = new ArrayList<>();
    }

    public Program(List<BlockStatement> blockStatements) {
        this.blockStatements = blockStatements;
    }

    public void addBlock(BlockStatement blockStatement) {
        this.blockStatements.add(blockStatement);
    }

    @Override
    public boolean accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
