package xyz.lebster.core.node;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Value;

public interface ASTNode {
	Value<?> execute(Interpreter interpreter) throws AbruptCompletion;

	SourceRange range();
}