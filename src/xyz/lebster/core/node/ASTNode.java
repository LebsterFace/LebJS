package xyz.lebster.core.node;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.Value;

public interface ASTNode {
	Value<?> execute(Interpreter interpreter) throws AbruptCompletion;

	void dump(int indent);

	void represent(StringRepresentation representation);

	default String toRepresentationString() {
		final StringRepresentation representation = new StringRepresentation();
		this.represent(representation);
		return representation.toString();
	}
}