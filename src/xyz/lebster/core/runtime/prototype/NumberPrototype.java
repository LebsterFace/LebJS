package xyz.lebster.core.runtime.prototype;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.NumberValue;
import xyz.lebster.core.node.value.StringValue;
import xyz.lebster.core.node.value.UndefinedValue;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.node.value.object.ObjectValue;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.error.TypeError;
import xyz.lebster.core.runtime.object.NumberWrapper;

public final class NumberPrototype extends ObjectValue {
	public static final NumberPrototype instance = new NumberPrototype();

	static {
		instance.setMethod(Names.toString, NumberPrototype::toStringMethod);
		instance.setMethod(Names.toLocaleString, NumberPrototype::toLocaleString);
	}

	private NumberPrototype() {
	}

	private static NumberValue thisNumberValue(Value<?> value) throws AbruptCompletion {
		// 1. If Type(value) is Number, return value.
		if (value instanceof final NumberValue numberValue) return numberValue;
		// 2. If Type(value) is Object and value has a [[NumberData]] internal slot, then
		if (value instanceof final NumberWrapper numberWrapper) {
			// a. Let n be value.[[NumberData]].
			// b. Assert: Type(n) is Number.
			// c. Return n.
			return numberWrapper.data;
		}
		// 3. Throw a TypeError exception.
		throw AbruptCompletion.error(new TypeError("This method requires that 'this' be a Number"));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-tointegerorinfinity")
	public static int toIntegerOrInfinity(Interpreter interpreter, Value<?> argument) throws AbruptCompletion {
		// 1. Let number be ? ToNumber(argument).
		final NumberValue numberValue = argument.toNumberValue(interpreter);
		// 2. If number is NaN, +0𝔽, or -0𝔽, return 0.
		if (numberValue.value.isNaN() || numberValue.value == 0) return 0;
		// 3. If number is +∞𝔽, return +∞.
		// 4. If number is -∞𝔽, return -∞.
		if (numberValue.value.isInfinite()) {
			if (numberValue.value > 0) {
				return Integer.MAX_VALUE;
			} else {
				return Integer.MIN_VALUE;
			}
		}
		// 5. Let integer be floor(abs(ℝ(number))).
		int integer = (int) Math.floor(Math.abs(numberValue.value));
		// 6. If number < +0𝔽, set integer to -integer.
		if (numberValue.value < 0) integer = -integer;
		// 7. Return integer.
		return integer;
	}

	// FIXME: Follow spec
	private static StringValue toLocaleString(Interpreter interpreter, Value<?>[] values) throws AbruptCompletion {
		final NumberValue x = thisNumberValue(interpreter.thisValue());
		return new StringValue(x.toLocaleString());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-number.prototype.tostring")
	private static StringValue toStringMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		final Value<?> radix = arguments.length > 0 ? arguments[0] : UndefinedValue.instance;
		// 1. Let x be ? thisNumberValue(this value).
		final NumberValue x = thisNumberValue(interpreter.thisValue());
		// 2. If radix is undefined, let radixMV be 10.
		// 3. Else, let radixMV be ? ToIntegerOrInfinity(radix).
		final int radixMV = toIntegerOrInfinity(interpreter, radix);
		// 4. If radixMV < 2 or radixMV > 36, throw a RangeError exception.
		if (radixMV < 2 || radixMV > 36)
			throw AbruptCompletion.error(new TypeError("toString() radix argument must be between 2 and 36"));
		// 5. If radixMV = 10, return ! ToString(x).
		if (radixMV == 10) return x.toStringValue(interpreter);
		// 6. Return the String representation of this Number value using the radix specified by radixMV. Letters a-z
		// are used for digits with values 10 through 35. The precise algorithm is implementation-defined, however the
		// algorithm should be a generalization of that specified in 6.1.6.1.20.
		// FIXME: Follow spec
		return x.toStringValue(interpreter);
	}
}