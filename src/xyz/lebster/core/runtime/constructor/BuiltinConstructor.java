package xyz.lebster.core.runtime.constructor;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.node.value.object.Constructor;
import xyz.lebster.core.node.value.object.ObjectValue;

abstract class BuiltinConstructor<T extends ObjectValue> extends Constructor<Void> {
	BuiltinConstructor() {
		super(null);
	}

	@Override
	public abstract T construct(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion;
}
