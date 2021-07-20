package xyz.lebster.node;

import xyz.lebster.ANSI;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.interpreter.StringRepresentation;
import xyz.lebster.node.value.Value;


public interface ASTNode {
	Value<?> execute(Interpreter interpreter) throws AbruptCompletion;

	default void represent(StringRepresentation representation) {
		representation.append(ANSI.BACKGROUND_BRIGHT_YELLOW);
		representation.append('(');
		representation.append(getClass().getSimpleName());
		representation.append(')');
		representation.append(ANSI.RESET);
	}

	void dump(int indent);
}