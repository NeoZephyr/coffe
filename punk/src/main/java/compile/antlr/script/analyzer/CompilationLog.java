package compile.antlr.script.analyzer;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * 记录编译过程中产生的信息
 */
public class CompilationLog {

    // 相关的 AST 节点
    public ParserRuleContext ctx;
    public String message;
    public int line;
    public int column;
    public LogLevel level = LogLevel.INFO;

    @Override
    public String toString() {
        return String.format("%s @%d:%d", message, line, column);
    }
}