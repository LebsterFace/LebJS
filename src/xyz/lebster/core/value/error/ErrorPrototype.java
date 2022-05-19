package xyz.lebster.core.value.error;

import xyz.lebster.core.value.BuiltinPrototype;
import xyz.lebster.core.value.object.ObjectPrototype;

public final class ErrorPrototype extends BuiltinPrototype<ErrorObject, ErrorConstructor> {
	public ErrorPrototype(ObjectPrototype objectPrototype) {
		super(objectPrototype);
	}
}
