package xyz.lebster.core.node;

import xyz.lebster.core.exception.LanguageException;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Value;

public interface ASTNode {
	void dump(int indent);

	Value<?> execute(Interpreter interpreter) throws LanguageException;
}
