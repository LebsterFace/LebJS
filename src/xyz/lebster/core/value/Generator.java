package xyz.lebster.core.value;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectPrototype;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-generator-objects")
// A Generator is an instance of a generator function and conforms to both the Iterator and Iterable interfaces.
// FIXME: Expose prototype
public abstract class Generator extends ObjectValue {
	public Generator(ObjectPrototype objectPrototype, FunctionPrototype functionPrototype) {
		super(objectPrototype);

		this.putMethod(functionPrototype, SymbolValue.iterator, (interpreter, arguments) -> this);
		this.putMethod(functionPrototype, Names.next, (interpreter, arguments) -> {
			final IteratorResult result = this.nextMethod(interpreter, arguments);

			final ObjectValue object = new ObjectValue(interpreter.intrinsics.objectPrototype);
			object.put(Names.done, BooleanValue.of(result.done()));
			object.put(Names.value, result.done() ? Undefined.instance : result.value());
			return object;
		});
	}

	/**
	 * If a previous call to the next method of an Iterator has returned an IteratorResult object whose "done" property is true,
	 * then all subsequent calls to the next method of that object should also return an IteratorResult object whose "done" property is true.
	 * However, this requirement is not enforced.
	 */
	public abstract IteratorResult nextMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion;
}
