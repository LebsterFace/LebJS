package xyz.lebster.core.node;

import xyz.lebster.core.runtime.AbruptCompletion;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Value;


public interface ASTNode {
	void dump(int indent);

	// TODO: Rename to 'evaluate'
	Value<?> execute(Interpreter interpreter) throws AbruptCompletion;
}
