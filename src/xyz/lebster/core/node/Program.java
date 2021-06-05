package xyz.lebster.core.node;

import xyz.lebster.core.Interpreter;
import xyz.lebster.core.exception.LanguageException;
import xyz.lebster.core.value.Undefined;
import xyz.lebster.core.value.Value;

public class Program extends ScopeNode {
    @Override
    public void dump(int indent) {
        Interpreter.dumpIndent(indent);
        System.out.println("Program:");
        for (ASTNode child : children) child.dump(indent + 1);
    }

    @Override
    public Value<?> execute(Interpreter interpreter) throws LanguageException {
        Value<?> lastValue = new Undefined();
        for (ASTNode node : children) lastValue = node.execute(interpreter);
        return lastValue;
    }
}
