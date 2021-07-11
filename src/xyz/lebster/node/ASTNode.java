package xyz.lebster.node;

import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.interpreter.StringRepresentation;
import xyz.lebster.node.value.Value;


public interface ASTNode {
	Value<?> execute(Interpreter interpreter) throws AbruptCompletion;

	void represent(StringRepresentation representation);

	void dump(int indent);
}