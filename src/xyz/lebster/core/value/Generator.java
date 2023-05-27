package xyz.lebster.core.value;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-generator-objects")
// A Generator is an instance of a generator function and conforms to both the Iterator and Iterable interfaces.
// FIXME: Expose prototype
public abstract class Generator extends ObjectValue {
	private boolean completed = false;

	public Generator(Intrinsics intrinsics) {
		super(intrinsics);

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

	protected void setCompleted() {
		completed = true;
	}

	// 27.5.1.2 Generator.prototype.next ( value )
	public abstract Value<?> next(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion;
}
