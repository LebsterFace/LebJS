package xyz.lebster.core.value.primitive.number;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.primitive.PrimitiveConstructor;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.function.NativeFunction.argument;

@NonStandard
@SpecificationURL("https://tc39.es/ecma262/multipage#sec-number-constructor")
public final class NumberConstructor extends PrimitiveConstructor {
	public NumberConstructor(Intrinsics intrinsics) {
		super(intrinsics, Names.Number);
		putMethod(intrinsics, Names.range, 3, NumberConstructor::range);
		putMethod(intrinsics, Names.isFinite, 1, NumberConstructor::isFinite);
		putMethod(intrinsics, Names.isInteger, 1, NumberConstructor::isInteger);
		putMethod(intrinsics, Names.isNaN, 1, NumberConstructor::isNaN);
		putMethod(intrinsics, Names.isSafeInteger, 1, NumberConstructor::isSafeInteger);
		// NOTE: Number.parseInt & Number.parseFloat are not included as they are identical to their global counterparts

		put(Names.EPSILON, NumberValue.EPSILON, false, false, false);
		put(Names.MAX_SAFE_INTEGER, NumberValue.MAX_SAFE_INTEGER, false, false, false);
		put(Names.MAX_VALUE, NumberValue.MAX_VALUE, false, false, false);
		put(Names.MIN_SAFE_INTEGER, NumberValue.MIN_SAFE_INTEGER, false, false, false);
		put(Names.MIN_VALUE, NumberValue.MIN_VALUE, false, false, false);
		put(Names.NaN, NumberValue.NaN, false, false, false);
		put(Names.NEGATIVE_INFINITY, NumberValue.NEGATIVE_INFINITY, false, false, false);
		put(Names.POSITIVE_INFINITY, NumberValue.POSITIVE_INFINITY, false, false, false);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-number.issafeinteger")
	private static BooleanValue isSafeInteger(Interpreter interpreter, Value<?>[] arguments) {
		// 21.1.2.5 Number.isSafeInteger ( number )
		final Value<?> number = argument(0, arguments);

		// 1. If IsIntegralNumber(number) is true, then
		// a. If abs(ℝ(number)) ≤ 2^53 - 1, return true.
		// 2. Return false.
		return BooleanValue.of(
			number instanceof final NumberValue N &&
			N.isIntegralNumber() &&
			Math.abs(N.value) <= NumberValue.MAX_SAFE_INTEGER.value
		);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-number.isnan")
	private static BooleanValue isNaN(Interpreter interpreter, Value<?>[] arguments) {
		// 21.1.2.4 Number.isNaN ( number )
		final Value<?> number = argument(0, arguments);

		// 1. If number is not a Number, return false.
		// 2. If number is NaN, return true.
		// 3. Otherwise, return false.
		return BooleanValue.of(number instanceof final NumberValue N && Double.isNaN(N.value));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-number.isinteger")
	private static BooleanValue isInteger(Interpreter interpreter, Value<?>[] arguments) {
		// 21.1.2.3 Number.isInteger ( number )
		final Value<?> number = argument(0, arguments);

		// 1. Return IsIntegralNumber(number).
		return BooleanValue.of(number instanceof final NumberValue N && N.isIntegralNumber());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-number.isfinite")
	private static BooleanValue isFinite(Interpreter interpreter, Value<?>[] arguments) {
		// 21.1.2.2 Number.isFinite ( number )
		final Value<?> number = argument(0, arguments);

		// 1. If number is not a Number, return false.
		// 2. If number is not finite, return false.
		// 3. Otherwise, return true.
		return BooleanValue.of(number instanceof final NumberValue N && Double.isFinite(N.value));
	}

	@NonStandard
	private static NumberRange range(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// Number.range(first: number, second: number, third: number): NumberRange
		if (arguments.length == 0) throw error(new TypeError(interpreter, "No end value was provided"));

		final double first = arguments[0].toNumberValue(interpreter).value;
		if (Double.isNaN(first)) throw error(new TypeError(interpreter, "NaN passed as first argument of Number.range"));
		if (arguments.length == 1) return new NumberRange(interpreter.intrinsics, first);

		final double second = arguments[1].toNumberValue(interpreter).value;
		if (Double.isNaN(second)) throw error(new TypeError(interpreter, "NaN passed as second argument of Number.range"));
		if (arguments.length == 2) return new NumberRange(interpreter.intrinsics, first, second);

		final double third = arguments[2].toNumberValue(interpreter).value;
		if (Double.isNaN(third)) throw error(new TypeError(interpreter, "NaN passed as third argument of Number.range"));
		return new NumberRange(interpreter.intrinsics, first, second, third);
	}

	@Override
	public NumberValue internalCall(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		// 21.1.1.1 Number ( value )
		final Value<?> value = argument(0, arguments);

		// 1. If value is present, then
		NumberValue n;
		if (arguments.length > 0) {
			// a. Let prim be ? ToNumeric(value).
			final NumberValue prim = value.toNumeric(interpreter);
			// TODO: b. If prim is a BigInt, let n be 𝔽(ℝ(prim)).
			// c. Otherwise, let n be prim.
			n = prim;
		}
		// 2. Else,
		else {
			// a. Let n be +0𝔽.
			n = NumberValue.ZERO;
		}

		// 3. If NewTarget is undefined, return n.
		return n;
	}
}
