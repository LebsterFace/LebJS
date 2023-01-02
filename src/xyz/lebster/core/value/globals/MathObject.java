package xyz.lebster.core.value.globals;

import xyz.lebster.core.Proposal;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.string.StringValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

import static xyz.lebster.core.value.function.NativeFunction.argumentDouble;
import static xyz.lebster.core.value.primitive.number.NumberValue.isNegativeZero;
import static xyz.lebster.core.value.primitive.number.NumberValue.isPositiveZero;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-math-object")
public final class MathObject extends ObjectValue {
	public MathObject(Intrinsics intrinsics) {
		super(intrinsics);
		put(SymbolValue.toStringTag, Names.Math);

		// 21.3.1 Value Properties of the Math Object
		addConstant(Names.E, Math.E); // e, the base of the natural logarithms
		addConstant(Names.LN10, Math.log(10)); // the natural logarithm of 10
		addConstant(Names.LN2, Math.log(2)); // the natural logarithm of 2
		addConstant(Names.LOG10E, Math.log10(Math.E)); // the base-10 logarithm of e, the base of the natural logarithms
		addConstant(Names.LOG2E, log2(Math.E)); // the base-2 logarithm of e, the base of the natural logarithms
		addConstant(Names.PI, Math.PI); // π, the ratio of the circumference of a circle to its diameter
		addConstant(Names.SQRT1_2, Math.sqrt(0.5)); // the square root of ½
		addConstant(Names.SQRT2, Math.sqrt(2)); // the square root of 2

		// 21.3.2 Function Properties of the Math Object

		// (double) -> boolean
		putMethod(intrinsics, Names.signbit, 1, MathObject::signbit);

		// (double) -> double
		addWrapper(intrinsics, Names.abs, (DoubleUnaryOperator) Math::abs);
		addWrapper(intrinsics, Names.acos, Math::acos);
		addWrapper(intrinsics, Names.asin, Math::asin);
		addWrapper(intrinsics, Names.atan, Math::atan);
		addWrapper(intrinsics, Names.ceil, Math::ceil);
		addWrapper(intrinsics, Names.cbrt, Math::cbrt);
		addWrapper(intrinsics, Names.expm1, Math::expm1);
		addWrapper(intrinsics, Names.cos, Math::cos);
		addWrapper(intrinsics, Names.cosh, Math::cosh);
		addWrapper(intrinsics, Names.exp, Math::exp);
		addWrapper(intrinsics, Names.floor, Math::floor);
		addWrapper(intrinsics, Names.log, Math::log);
		addWrapper(intrinsics, Names.log1p, Math::log1p);
		addWrapper(intrinsics, Names.log10, Math::log10);
		addWrapper(intrinsics, Names.round, (DoubleUnaryOperator) Math::round);
		addWrapper(intrinsics, Names.sin, Math::sin);
		addWrapper(intrinsics, Names.sinh, Math::sinh);
		addWrapper(intrinsics, Names.sqrt, Math::sqrt);
		addWrapper(intrinsics, Names.tan, Math::tan);
		addWrapper(intrinsics, Names.tanh, Math::tanh);
		addWrapper(intrinsics, Names.sign, (DoubleUnaryOperator) Math::signum);

		// (double, double) -> double
		addWrapper(intrinsics, Names.atan2, Math::atan2);
		addWrapper(intrinsics, Names.pow, Math::pow);

		// (...double[]) -> double
		addWrapper(intrinsics, Names.hypot, MathObject::hypot);
		addWrapper(intrinsics, Names.max, MathObject::max);
		addWrapper(intrinsics, Names.min, MathObject::min);

		// () -> double
		putMethod(intrinsics, Names.random, 0, MathObject::random);
		addWrapper(intrinsics, Names.trunc, MathObject::trunc);
		addWrapper(intrinsics, Names.asinh, MathObject::asinh);
		addWrapper(intrinsics, Names.log2, MathObject::log2);
		addWrapper(intrinsics, Names.acosh, MathObject::acosh);
		addWrapper(intrinsics, Names.atanh, MathObject::atanh);

		// Not implemented
		notImplemented(intrinsics, "imul", 2);
		notImplemented(intrinsics, "clz32", 1);
		notImplemented(intrinsics, "fround", 1);
	}

	private void notImplemented(Intrinsics intrinsics, String methodName, int expectedArgumentCount) {
		putMethod(intrinsics, new StringValue(methodName), expectedArgumentCount, (interpreter, args) -> {
			throw new NotImplemented(methodName);
		});
	}

	private void addConstant(StringValue name, double value) {
		put(name, new NumberValue(value), false, false, false);
	}

	// For https://tc39.es/ecma262/multipage#sec-math.hypot + sec-math.min + sec-math.max
	private void addWrapper(Intrinsics intrinsics, StringValue methodName, DoubleRestArgs restArgs) {
		putMethod(intrinsics, methodName, 0, (interpreter, args) -> {
			final double[] coerced = new double[args.length];
			for (int i = 0; i < args.length; i++) {
				coerced[i] = args[i].toNumberValue(interpreter).value;
			}

			return new NumberValue(restArgs.applyAsDouble(coerced));
		});
	}

	private void addWrapper(Intrinsics intrinsics, StringValue methodName, DoubleUnaryOperator unaryOperator) {
		putMethod(intrinsics, methodName, 1, (interpreter, args) -> {
			final double number = argumentDouble(0, interpreter, args);
			return new NumberValue(unaryOperator.applyAsDouble(number));
		});
	}

	private void addWrapper(Intrinsics intrinsics, StringValue methodName, DoubleBinaryOperator binaryOperator) {
		putMethod(intrinsics, methodName, 2, (interpreter, args) -> {
			final double a = argumentDouble(0, interpreter, args);
			final double b = argumentDouble(1, interpreter, args);
			return new NumberValue(binaryOperator.applyAsDouble(a, b));
		});
	}

	@Proposal
	@SpecificationURL("https://tc39.es/proposal-Math.signbit/Math.signbit.html#spec")
	private static BooleanValue signbit(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		final double n = argumentDouble(0, interpreter, arguments);
		// 1. If n is NaN, the result is false.
		if (Double.isNaN(n)) return BooleanValue.FALSE;
		// 2. If n is -0, the result is true.
		if (NumberValue.isNegativeZero(n)) return BooleanValue.TRUE;
		// 3. If n is negative, the result is true.
		if (n < 0) return BooleanValue.TRUE;
		// 4. Otherwise, the result is false.
		return BooleanValue.FALSE;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-math.trunc")
	private static double trunc(double n) {
		// 2. If n is NaN, n is +0𝔽, n is -0𝔽, n is +∞𝔽, or n is -∞𝔽, return n.
		if (Double.isNaN(n) || n == 0 || Double.isInfinite(n)) {
			return n;
		}
		// 3. If n < 1𝔽 and n > +0𝔽, return +0𝔽.
		else if (n < 1.0D && n > 0.0D) {
			return 0.0D;
		}
		// 4. If n < +0𝔽 and n > -1𝔽, return -0𝔽.
		else if (n < 0 && n > -1.0D) {
			return -0.0D;
		}
		// 5. Return the integral Number nearest n in the direction of +0𝔽.
		else if (n > 0.0D) {
			return Math.floor(n);
		} else {
			return Math.ceil(n);
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-math.min")
	private static double min(double[] coerced) {
		//  3. Let lowest be +∞𝔽.
		double lowest = Double.POSITIVE_INFINITY;
		//  4. For each element number of coerced, do
		for (double number : coerced) {
			// a. If number is NaN, return NaN.
			if (Double.isNaN(number)) return Double.NaN;
				// b. If number is -0𝔽 and lowest is +0𝔽, set lowest to -0𝔽.
			else if (isNegativeZero(number) && isPositiveZero(lowest))
				lowest = -0.0;
				// c. If number < lowest, set lowest to number.
			else if (number < lowest) lowest = number;
		}

		// 5. Return lowest.
		return lowest;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-math.max")
	private static double max(double[] coerced) {
		//  3. Let highest be -∞𝔽.
		double highest = Double.NEGATIVE_INFINITY;
		//  4. For each element number of coerced, do
		for (double number : coerced) {
			// a. If number is NaN, return NaN.
			if (Double.isNaN(number)) return Double.NaN;
				// b. If number is +0𝔽 and highest is -0𝔽, set highest to +0𝔽.
			else if (isPositiveZero(number) && isNegativeZero(highest))
				highest = 0.0;

				// c. If number > highest, set highest to number.
			else if (number > highest) highest = number;
		}

		// 5. Return highest.
		return highest;
	}

	private static NumberValue random(Interpreter interpreter, Value<?>[] arguments) {
		return new NumberValue(Math.random());
	}

	@SpecificationURL("https://github.com/zloirock/core-js/blob/master/packages/core-js/modules/es.math.asinh.js")
	private static double asinh(double n) {
		if (Double.isNaN(n) || Double.isInfinite(n) || n == 0) return n;
		if (n < 0) return -asinh(-n);
		return Math.log(n + Math.sqrt(n * n + 1));
	}

	private static double log2(double x) {
		return Math.log(x) / Math.log(2);
	}

	private static double acosh(double x) {
		return Math.log(x + Math.sqrt(x * x - 1));
	}

	private static double atanh(double n) {
		return n == 0 ? n : Math.log((1 + n) / (1 - n)) / 2;
	}

	// This method conforms to the same interface as the specification, but doesn't
	// follow it exactly. It should behave the same for any given input, however.
	private static double hypot(double[] coerced) {
		double sum = 0;
		for (final double number : coerced) {
			if (Double.isInfinite(number)) return Double.POSITIVE_INFINITY;
			else if (Double.isNaN(number)) return Double.NaN;
			else {
				sum += number * number;
			}
		}

		// Return the square root of the sum of squares of the elements of coerced.
		return Math.sqrt(sum);
	}

	@FunctionalInterface
	private interface DoubleRestArgs {
		double applyAsDouble(double[] coerced);
	}
}