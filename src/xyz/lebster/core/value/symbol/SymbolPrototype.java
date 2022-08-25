package xyz.lebster.core.value.symbol;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.BuiltinPrototype;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.TypeError;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.object.NativeAccessorDescriptor;
import xyz.lebster.core.value.object.ObjectPrototype;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.string.StringValue;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-symbol-prototype-object")
public final class SymbolPrototype extends BuiltinPrototype<SymbolWrapper, SymbolConstructor> {
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-symbol.prototype.description")
	private static final NativeAccessorDescriptor DESCRIPTION = new NativeAccessorDescriptor(false, true) {
		@Override
		public Value<?> get(Interpreter interpreter, ObjectValue thisValue) throws AbruptCompletion {
			// 1. Let s be the `this` value.
			// 2. Let sym be ? thisSymbolValue(s).
			final SymbolValue sym = thisSymbolValue(interpreter, thisValue, "Symbol.prototype.description");
			// 3. Return sym.[[Description]].
			return sym.description;
		}

		@Override
		public void set(Interpreter interpreter, ObjectValue thisValue, Value<?> newValue) {
		}
	};

	public SymbolPrototype(ObjectPrototype objectPrototype, FunctionPrototype fp) {
		super(objectPrototype);
		this.value.put(Names.description, SymbolPrototype.DESCRIPTION);
		putMethod(fp, Names.toString, SymbolPrototype::toStringMethod);
		putMethod(fp, Names.valueOf, SymbolPrototype::valueOf);
		putMethod(fp, SymbolValue.toPrimitive, SymbolPrototype::toPrimitiveMethod);
		putNonWritable(SymbolValue.toStringTag, Names.Symbol);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-symbol.prototype.valueof")
	private static SymbolValue valueOf(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Return ? thisSymbolValue(this value).
		return thisSymbolValue(interpreter, interpreter.thisValue(), "Symbol.prototype.valueOf");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-symbol.prototype-@@toprimitive")
	private static SymbolValue toPrimitiveMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Return ? thisSymbolValue(this value).
		return thisSymbolValue(interpreter, interpreter.thisValue(), "Symbol.prototype [ @@toPrimitive ]");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-symbol.prototype.tostring")
	private static StringValue toStringMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.4.3.3 Symbol.prototype.toString ( )

		// 1. Let sym be ? thisSymbolValue(this value).
		final SymbolValue sym = thisSymbolValue(interpreter, interpreter.thisValue(), "Symbol.prototype.toString");
		// 2. Return SymbolDescriptiveString(sym).
		return new StringValue(sym.symbolDescriptiveString());
	}

	private static SymbolValue thisSymbolValue(Interpreter interpreter, Value<?> value, String methodName) throws AbruptCompletion {
		// 1. If Type(value) is Symbol, return value.
		if (value instanceof final SymbolValue symbolValue) return symbolValue;
		// 2. If Type(value) is Object and value has a [[SymbolData]] internal slot, then
		if (value instanceof final SymbolWrapper symbolWrapper) {
			// a. Let s be value.[[SymbolData]].
			// b. Assert: Type(s) is Symbol.
			// c. Return s.
			return symbolWrapper.data;
		}

		// 3. Throw a TypeError exception.
		final String message = methodName + " requires that 'this' be a Symbol";
		throw AbruptCompletion.error(new TypeError(interpreter, message));
	}
}