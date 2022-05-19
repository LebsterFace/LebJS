package xyz.lebster.core.runtime.value.error;

import xyz.lebster.core.runtime.value.prototype.BuiltinPrototype;
import xyz.lebster.core.runtime.value.prototype.ObjectPrototype;

public final class ErrorPrototype extends BuiltinPrototype<ErrorObject, ErrorConstructor> {
	public ErrorPrototype(ObjectPrototype objectPrototype) {
		super(objectPrototype);
	}
}
