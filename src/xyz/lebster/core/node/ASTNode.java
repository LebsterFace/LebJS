package xyz.lebster.core.node;

import xyz.lebster.exception.LanguageException;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Value;

public interface ASTNode {
	void dump(int indent);
// TODO: Rename to 'evaluate'
	Value<?> execute(Interpreter interpreter) throws LanguageException;
}
