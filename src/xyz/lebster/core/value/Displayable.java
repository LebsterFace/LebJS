package xyz.lebster.core.value;

import xyz.lebster.core.interpreter.StringRepresentation;

public interface Displayable {
	void display(StringRepresentation representation);

	default String toDisplayString() {
		final var representation = new StringRepresentation();
		this.display(representation);
		return representation.toString();
	}
}