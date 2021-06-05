package xyz.lebster.node;

import xyz.lebster.Interpreter;
import xyz.lebster.exception.LanguageException;
import xyz.lebster.value.Value;

public interface ASTNode {
    void dump(int indent);
    Value<?> execute(Interpreter interpreter) throws LanguageException;
}
