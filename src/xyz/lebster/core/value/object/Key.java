package xyz.lebster.core.value.object;

import xyz.lebster.core.value.primitive.PrimitiveValue;
import xyz.lebster.core.value.primitive.string.StringValue;

public abstract class Key<R> extends PrimitiveValue<R> {
	public Key(R value) {
		super(value);
	}

	public void displayForObjectKey(StringBuilder builder) {
		this.display(builder);
	}

	public abstract StringValue toFunctionName();

	public abstract int toIndex();

	public abstract boolean equalsKey(Key<?> other);
}
