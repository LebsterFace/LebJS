package xyz.lebster.core.value.primitive.symbol;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-symbol-prototype-object")
public final class SymbolPrototype extends ObjectValue {
	public SymbolPrototype(Intrinsics intrinsics) {
		super(intrinsics);
		putMethod(intrinsics, Names.toString, 0, SymbolPrototype::toStringMethod);
		putMethod(intrinsics, Names.valueOf, 0, SymbolPrototype::valueOf);
		putMethod(intrinsics, SymbolValue.toPrimitive, 1, SymbolPrototype::toPrimitiveMethod);
		put(SymbolValue.toStringTag, Names.Symbol, false, false, true);
		putAccessor(intrinsics, Names.description, SymbolPrototype::getDescription, null, false, true);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-symbol.prototype.description")
	private static Value<?> getDescription(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Let s be the `this` value.
		// 2. Let sym be ? thisSymbolValue(s).
		final SymbolValue sym = thisSymbolValue(interpreter);
		// 3. Return sym.[[Description]].
		return sym.description;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-symbol.prototype.valueof")
	private static SymbolValue valueOf(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.4.3.4 Symbol.prototype.valueOf ( )

		// 1. Return ? thisSymbolValue(this value).
		return thisSymbolValue(interpreter);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-symbol.prototype-@@toprimitive")
	private static SymbolValue toPrimitiveMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.4.3.5 Symbol.prototype [ @@toPrimitive ] ( hint )

		// 1. Return ? thisSymbolValue(this value).
		return thisSymbolValue(interpreter);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-symbol.prototype.tostring")
	private static StringValue toStringMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.4.3.3 Symbol.prototype.toString ( )

		// 1. Let sym be ? thisSymbolValue(this value).
		final SymbolValue sym = thisSymbolValue(interpreter);
		// 2. Return SymbolDescriptiveString(sym).
		return new StringValue(sym.symbolDescriptiveString());
	}

	private static SymbolValue thisSymbolValue(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> value = interpreter.thisValue();
		// 1. If Type(value) is Symbol, return value.
		if (value instanceof SymbolValue symbolValue) return symbolValue;
		// 2. If Type(value) is Object and value has a [[SymbolData]] internal slot, then
		if (value instanceof SymbolWrapper symbolWrapper) return symbolWrapper.data;
		// 3. Throw a TypeError exception.
		throw error(interpreter.incompatibleReceiver("Symbol.prototype", "a Symbol"));
	}
}