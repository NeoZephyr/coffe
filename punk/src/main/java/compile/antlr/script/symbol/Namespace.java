package compile.antlr.script.symbol;

import lombok.Getter;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Namespace extends Block {
    private Namespace parent = null;
    private List<Namespace> children = new LinkedList<>();

    @Getter
    private String name = null;

    public Namespace(String name, Scope enclosingScope, ParserRuleContext ctx) {
        this.name = name;
        this.enclosingScope = enclosingScope;
        this.ctx = ctx;
    }

    public List<Namespace> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void addChild(Namespace child) {
        child.parent = this;
        children.add(child);
    }

    public void removeChild(Namespace child) {
        child.parent = null;
        children.remove(child);
    }

}