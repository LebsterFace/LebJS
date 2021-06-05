package xyz.lebster.node;
import xyz.lebster.Interpreter;
import xyz.lebster.value.Value;

import java.util.ArrayList;

abstract public class ScopeNode implements ASTNode {
    public ArrayList<ASTNode> children = new ArrayList<>();

    public ScopeNode append(ASTNode node) {
        this.children.add(node);
        return this;
    }
}
