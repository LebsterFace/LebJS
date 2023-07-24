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
	private static final double RAD_PER_DEG = 180 / Math.PI;
	private static final double DEG_PER_RAD = Math.PI / 180;

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
		addConstant(Names.RAD_PER_DEG, RAD_PER_DEG);
		addConstant(Names.DEG_PER_RAD, DEG_PER_RAD);

		// 21.3.2 Function Properties of the Math Object

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
		addWrapper(intrinsics, Names.max, (DoubleRestArgs) MathObject::max);
		addWrapper(intrinsics, Names.min, (DoubleRestArgs) MathObject::min);

		// () -> double
		putMethod(intrinsics, Names.random, 0, MathObject::random);
		addWrapper(intrinsics, Names.trunc, MathObject::trunc);
		addWrapper(intrinsics, Names.asinh, MathObject::asinh);
		addWrapper(intrinsics, Names.log2, MathObject::log2);
		addWrapper(intrinsics, Names.acosh, MathObject::acosh);
		addWrapper(intrinsics, Names.atanh, MathObject::atanh);

		// Proposed methods
		putMethod(intrinsics, Names.signbit, 1, MathObject::signbit);
		putMethod(intrinsics, Names.clamp, 3, MathObject::clamp);
		putMethod(intrinsics, Names.scale, 5, MathObject::scale);
		putMethod(intrinsics, Names.radians, 1, MathObject::radians);
		putMethod(intrinsics, Names.degrees, 1, MathObject::degrees);

		// Not implemented
		notImplemented(intrinsics, "imul", 2);
		notImplemented(intrinsics, "clz32", 1);
		notImplemented(intrinsics, "fround", 1);
	}

	@Proposal
	@SpecificationURL("https://rwaldron.github.io/proposal-math-extensions/#sec-math.degrees")
	private static NumberValue degrees(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 5 Math.degrees ( radians )
		final double radians = argumentDouble(0, interpreter, arguments);

		// 1. If radians is one of NaN, +∞, -∞, return radians.
		if (Double.isNaN(radians) || Double.isInfinite(radians)) return new NumberValue(radians);
		// 2. Let degrees be (radians × Math.RAD_PER_DEG).
		final double degrees = radians * RAD_PER_DEG;
		// 3. Return degrees.
		return new NumberValue(degrees);
	}

	@Proposal
	@SpecificationURL("https://rwaldron.github.io/proposal-math-extensions/#sec-math.radians")
	private static NumberValue radians(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 4. Math.radians ( degrees )
		final double degrees = argumentDouble(0, interpreter, arguments);

		// 1. If degrees is one of NaN, +∞, -∞, return degrees.
		if (Double.isNaN(degrees) || Double.isInfinite(degrees)) return new NumberValue(degrees);
		// 2. Let radians be (degrees × Math.DEG_PER_RAD).
		final double radians = degrees * DEG_PER_RAD;
		// 3. Return radians.
		return new NumberValue(radians);
	}

	@Proposal
	@SpecificationURL("https://rwaldron.github.io/proposal-math-extensions/#sec-math.scale")
	private static NumberValue scale(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 3 Math.scale ( x, inLow, inHigh, outLow, outHigh )
		final double x = argumentDouble(0, interpreter, arguments);
		final double inLow = argumentDouble(1, interpreter, arguments);
		final double inHigh = argumentDouble(2, interpreter, arguments);
		final double outLow = argumentDouble(3, interpreter, arguments);
		final double outHigh = argumentDouble(4, interpreter, arguments);

		// 1. If any argument is NaN, return NaN.
		if (Double.isNaN(x) || Double.isNaN(inLow) || Double.isNaN(inHigh) || Double.isNaN(outLow) || Double.isNaN(outHigh))
			return NumberValue.NaN;

		// 2. If x is one of +∞, -∞, return x.
		if (Double.isInfinite(x)) return new NumberValue(x);
		// 3. Return (x − inLow) × (outHigh − outLow) ÷ (inHigh − inLow) + outLow.
		return new NumberValue((x - inLow) * (outHigh - outLow) / (inHigh - inLow) + outLow);
	}

	@Proposal
	@SpecificationURL("https://rwaldron.github.io/proposal-math-extensions/#sec-math.clamp")
	private static NumberValue clamp(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 2 Math.clamp ( x, lower, upper )

		final double x = argumentDouble(0, interpreter, arguments);
		final double lower = argumentDouble(1, interpreter, arguments);
		final double upper = argumentDouble(2, interpreter, arguments);

		// 1. If any argument is NaN, return NaN.
		if (Double.isNaN(x) || Double.isNaN(lower) || Double.isNaN(upper)) return NumberValue.NaN;
		// 2. Let max be %Math_max%(x, lower).
		final double max = max(x, lower);
		// 3. Let min be %Math_min%(max, upper).
		final double min = min(max, upper);
		// 4. Return min.
		return new NumberValue(min);
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
		// 2. If n is not finite or n is either +0𝔽 or -0𝔽, return n.
		if (!Double.isFinite(n) || n == 0) return n;
		// 3. If n < 1𝔽 and n > +0𝔽, return +0𝔽.
		if (n < 1.0D && n > 0.0D) return 0.0D;
		// 4. If n < -0𝔽 and n > -1𝔽, return -0𝔽.
		if (n < -0.0D && n > -1.0D) return -0.0D;
		// 5. Return the integral Number nearest n in the direction of +0𝔽.
		return NumberValue.truncate(n);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-math.min")
	private static double min(double... coerced) {
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
	private static double max(double... coerced) {
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

	@FunctionalInterface
	private interface DoubleRestArgs {
		double applyAsDouble(double[] coerced);
	}
}