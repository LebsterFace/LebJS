package xyz.lebster.core.node;

import xyz.lebster.core.exception.LanguageException;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.runtime.ScopeFrame;
import xyz.lebster.core.value.Undefined;
import xyz.lebster.core.value.Value;

import java.util.ArrayList;

abstract public class ScopeNode implements ASTNode {
    public ArrayList<ASTNode> children = new ArrayList<>();

    public ScopeNode append(ASTNode node) {
        this.children.add(node);
        return this;
    }
}
