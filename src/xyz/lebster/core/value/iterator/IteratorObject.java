package xyz.lebster.core.value.iterator;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

public abstract class IteratorObject extends ObjectValue {
	private boolean completed = false;

	public IteratorObject(Intrinsics intrinsics) {
		super(intrinsics.iteratorPrototype);

		putMethod(intrinsics, SymbolValue.iterator, 0, (interpreter, arguments) -> this);
		putMethod(intrinsics, Names.next, 1, this::nextMethod);
	}

	private ObjectValue nextMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		final ObjectValue object = new ObjectValue(interpreter.intrinsics);
		final var value = completed ? Undefined.instance : next(interpreter, arguments);
		object.set(interpreter, Names.value, value);
		object.set(interpreter, Names.done, BooleanValue.of(completed));
		return object;
	}

	protected Undefined setCompleted() {
		completed = true;
		return Undefined.instance;
	}

	public abstract Value<?> next(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion;
}
