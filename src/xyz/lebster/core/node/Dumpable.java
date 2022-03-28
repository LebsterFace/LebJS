package xyz.lebster.core.node;

import xyz.lebster.core.interpreter.StringRepresentation;

public interface Dumpable {
	void dump(int indent);

	void represent(StringRepresentation representation);

	default String toRepresentationString() {
		final StringRepresentation representation = new StringRepresentation();
		this.represent(representation);
		return representation.toString();
	}
}