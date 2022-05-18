package xyz.lebster.core.runtime.value.prototype;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.constructor.SymbolConstructor;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.object.SymbolWrapper;
import xyz.lebster.core.runtime.value.object.property.NativeAccessorDescriptor;
import xyz.lebster.core.runtime.value.primitive.SymbolValue;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-symbol-prototype-object")
public final class SymbolPrototype extends BuiltinPrototype<SymbolWrapper, SymbolConstructor> {
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-symbol.prototype.description")
	private static final NativeAccessorDescriptor DESCRIPTION = new NativeAccessorDescriptor(false, true) {
		@Override
		public Value<?> get(Interpreter interpreter, ObjectValue thisValue) throws AbruptCompletion {
			// 1. Let s be the this value.
			// 2. Let sym be ? thisSymbolValue(s).
			final SymbolValue sym = thisSymbolValue(interpreter, thisValue, "Symbol.prototype.description");
			// 3. Return sym.[[Description]].
			return sym.description;
		}

		@Override
		public void set(Interpreter interpreter, ObjectValue thisValue, Value<?> newValue) {
		}
	};

	public SymbolPrototype(ObjectPrototype objectPrototype) {
		super(objectPrototype);
		this.value.put(Names.description, SymbolPrototype.DESCRIPTION);
	}

	private static SymbolValue thisSymbolValue(Interpreter interpreter, Value<?> value, String methodName) throws AbruptCompletion {
		// 1. If Type(value) is Symbol, return value.
		if (value instanceof final SymbolValue symbolValue) return symbolValue;
		// 2. If Type(value) is Object and value has a [[SymbolData]] internal slot, then
		// a. Let s be value.[[SymbolData]].
		// b. Assert: Type(s) is Symbol.
		// c. Return s.
		if (value instanceof final SymbolWrapper symbolWrapper) return symbolWrapper.data;
		// 3. Throw a TypeError exception.
		throw AbruptCompletion.error(new TypeError(interpreter, methodName + " requires that 'this' be a Symbol"));
	}
}