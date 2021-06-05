package xyz.lebster.node;

import xyz.lebster.Interpreter;
import xyz.lebster.exception.LanguageException;
import xyz.lebster.value.Undefined;
import xyz.lebster.value.Value;

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
