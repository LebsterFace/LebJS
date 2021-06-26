package xyz.lebster.core.node;

import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Value;
import xyz.lebster.exception.LanguageException;

public interface ASTNode {
	void dump(int indent);

	// TODO: Rename to 'evaluate'
	Value<?> execute(Interpreter interpreter) throws LanguageException;
}
