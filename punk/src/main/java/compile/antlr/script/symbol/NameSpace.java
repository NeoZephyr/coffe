package compile.antlr.script.symbol;

import lombok.Getter;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class NameSpace extends Block {
    private NameSpace parent = null;
    private List<NameSpace> children = new LinkedList<>();

    @Getter
    private String name = null;

    protected NameSpace(String name, Scope enclosingScope, ParserRuleContext ctx) {
        this.name = name;
        this.enclosingScope = enclosingScope;
        this.ctx = ctx;
    }

    public List<NameSpace> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void addChild(NameSpace child) {
        child.parent = this;
        children.add(child);
    }

    public void removeChild(NameSpace child) {
        child.parent = null;
        children.remove(child);
    }

}