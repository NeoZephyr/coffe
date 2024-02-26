```java
import java.util.ArrayList;

class ASTNode {
    ArrayList<ASTNode> children;
    ASTNode parent;
    
    Token token;
    String label;
    ASTNodeType type;
}

enum ASTNodeType {
    BLOCK
}
```
