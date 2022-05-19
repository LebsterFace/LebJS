package xyz.lebster.core.value;

import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.object.ObjectPrototype;
import xyz.lebster.core.value.object.ObjectValue;

public class BuiltinPrototype<T extends ObjectValue, C extends Executable> extends ObjectValue {
	public BuiltinPrototype(ObjectPrototype objectPrototype) {
		super(objectPrototype);
	}

	private BuiltinPrototype() {
		super(null);
	}
}
