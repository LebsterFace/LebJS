package xyz.lebster.core.runtime.value.prototype;

import xyz.lebster.core.runtime.value.executable.Executable;
import xyz.lebster.core.runtime.value.object.ObjectValue;

public class BuiltinPrototype<T extends ObjectValue, C extends Executable> extends ObjectValue {
	public BuiltinPrototype(ObjectPrototype objectPrototype) {
		super(objectPrototype);
	}

	private BuiltinPrototype() {
		super(null);
	}
}
