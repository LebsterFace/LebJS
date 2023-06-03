package xyz.lebster.core.value.object;

import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.primitive.PrimitiveValue;
import xyz.lebster.core.value.primitive.string.StringValue;

public abstract class Key<R> extends PrimitiveValue<R> {
	public Key(R value) {
		super(value);
	}

	public void displayForObjectKey(StringRepresentation representation) {
		this.display(representation);
	}

	public abstract StringValue toFunctionName();

	public abstract int toIndex();

	public abstract boolean equalsKey(Key<?> other);
}
