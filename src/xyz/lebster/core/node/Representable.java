package xyz.lebster.core.node;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.StringRepresentation;

public interface Representable {
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
