package xyz.lebster.core.runtime.value.prototype;

import xyz.lebster.core.runtime.value.constructor.BuiltinConstructor;
import xyz.lebster.core.runtime.value.object.ObjectValue;

public class BuiltinPrototype<T extends ObjectValue, C extends BuiltinConstructor<T, ?>> extends ObjectValue {
	public BuiltinPrototype(ObjectPrototype objectPrototype) {
		super(objectPrototype);
	}

	private BuiltinPrototype() {
		super(null);
	}
}
