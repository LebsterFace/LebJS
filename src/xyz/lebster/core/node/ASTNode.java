package xyz.lebster.core.node;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.Value;

public interface ASTNode {
	Value<?> execute(Interpreter interpreter) throws AbruptCompletion;

	void dump(int indent);

	default void represent(StringRepresentation representation) {
		representation.append(ANSI.BACKGROUND_BRIGHT_YELLOW);
		representation.append('(');
		representation.append(getClass().getSimpleName());
		representation.append(')');
		representation.append(ANSI.RESET);
	}

	default String toRepresentationString() {
		final StringRepresentation representation = new StringRepresentation();
		this.represent(representation);
		return representation.toString();
	}
}