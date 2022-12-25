package xyz.lebster.core.value;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-generator-objects")
// A Generator is an instance of a generator function and conforms to both the Iterator and Iterable interfaces.
// FIXME: Expose prototype
public abstract class Generator extends ObjectValue {
	public Generator(Intrinsics intrinsics) {
		super(intrinsics);

		putMethod(intrinsics, SymbolValue.iterator, 0, (interpreter, arguments) -> this);
		putMethod(intrinsics, Names.next, 1, this::next);
	}

	// 27.5.1.2 Generator.prototype.next ( value )
	public abstract ObjectValue next(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion;
}
