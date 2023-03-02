package xyz.lebster.core.node;

import xyz.lebster.core.interpreter.StringRepresentation;

public interface Representable {
	void represent(StringRepresentation representation);

	default String toRepresentationString() {
		final StringRepresentation representation = new StringRepresentation();
		this.represent(representation);
		return representation.toString();
	}
}