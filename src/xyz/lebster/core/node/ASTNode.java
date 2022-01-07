package xyz.lebster.core.node;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.Value;

public interface ASTNode extends Representable {
	Value<?> execute(Interpreter interpreter) throws AbruptCompletion;

	void dump(int indent);
}