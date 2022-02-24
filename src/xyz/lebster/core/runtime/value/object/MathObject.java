package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.primitive.NumberValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.primitive.SymbolValue;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

import static xyz.lebster.core.runtime.value.native_.NativeFunction.argumentDouble;
import static xyz.lebster.core.runtime.value.primitive.NumberValue.isNegativeZero;
import static xyz.lebster.core.runtime.value.primitive.NumberValue.isPositiveZero;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-math-object")
public final class MathObject extends ObjectValue {
	public static final MathObject instance = new MathObject();

	@SuppressWarnings("SpellCheckingInspection")
	private MathObject() {
		put(SymbolValue.toStringTag, Names.Math);

		// 21.3.1 Value Properties of the Math Object
		this.putFrozen(Names.E, new NumberValue(2.7182818284590452354D)); // e, the base of the natural logarithms
		this.putFrozen(Names.LN10, new NumberValue(2.302585092994046D)); // the natural logarithm of 10
		this.putFrozen(Names.LN2, new NumberValue(0.6931471805599453D)); // the natural logarithm of 2
		this.putFrozen(Names.LOG10E, new NumberValue(0.4342944819032518D)); // the base-10 logarithm of e, the base of the natural logarithms
		this.putFrozen(Names.LOG2E, new NumberValue(1.4426950408889634D)); // the base-2 logarithm of e, the base of the natural logarithms
		this.putFrozen(Names.PI, new NumberValue(3.1415926535897932D)); // π, the ratio of the circumference of a circle to its diameter
		this.putFrozen(Names.SQRT1_2, new NumberValue(0.7071067811865476D)); // the square root of ½
		this.putFrozen(Names.SQRT2, new NumberValue(1.4142135623730951D)); // the square root of 2

		// (double) -> double
		addWrapper("abs", (DoubleUnaryOperator) Math::abs);
		addWrapper("acos", Math::acos);
		addWrapper("asin", Math::asin);
		addWrapper("atan", Math::atan);
		addWrapper("ceil", Math::ceil);
		addWrapper("cbrt", Math::cbrt);
		addWrapper("expm1", Math::expm1);
		addWrapper("cos", Math::cos);
		addWrapper("cosh", Math::cosh);
		addWrapper("exp", Math::exp);
		addWrapper("floor", Math::floor);
		addWrapper("log", Math::log);
		addWrapper("log1p", Math::log1p);
		addWrapper("log10", Math::log10);
		addWrapper("round", (DoubleUnaryOperator) Math::round);
		addWrapper("sin", Math::sin);
		addWrapper("sinh", Math::sinh);
		addWrapper("sqrt", Math::sqrt);
		addWrapper("tan", Math::tan);
		addWrapper("tanh", Math::tanh);
		addWrapper("sign", (DoubleUnaryOperator) Math::signum);

		// (double, double) -> double
		addWrapper("atan2", Math::atan2);
		addWrapper("pow", Math::pow);

		// This method conforms to the same interface as the specification, but doesn't
		// follow it exactly. It should behave the same for any given input, however.
		addWrapper("hypot", (double[] coerced) -> {
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
		});

		// https://tc39.es/ecma262/multipage#sec-math.max
		addWrapper("max", (double[] coerced) -> {
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
		});

		// https://tc39.es/ecma262/multipage#sec-math.min
		addWrapper("min", (double[] coerced) -> {
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
		});

		// () -> double
		this.putMethod(Names.random, (interpreter, args) -> new NumberValue(Math.random()));

		// https://tc39.es/ecma262/multipage#sec-math.trunc
		addWrapper("trunc", (double n) -> {
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
		});

		// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Math/asinh#polyfill
		addWrapper("asinh", (double x) -> {
			double absX = Math.abs(x), w;

			if (absX < 3.725290298461914e-9) // |x| < 2^-28
				return x;

			if (absX > 268435456) // |x| > 2^28
				w = Math.log(absX) + 0.6931471805599453D;

			else if (absX > 2) // 2^28 >= |x| > 2
				w = Math.log(2 * absX + 1 / (Math.sqrt(x * x + 1) + absX));
			else {
				double t = x * x;
				w = Math.log1p(absX + t / (1 + Math.sqrt(1 + t)));
			}

			return x > 0 ? w : -w;
		});

		addWrapper("log2", (double x) -> Math.log(x) / 0.6931471805599453D);
		addWrapper("acosh", (double x) -> Math.log(x + Math.sqrt(x * x - 1)));
		addWrapper("atanh", (double x) -> Math.log((1 + x) / (1 - x)) / 2);

		notImplemented("imul");
		notImplemented("clz32");
		notImplemented("fround");
	}

	private void notImplemented(String methodName) {
		this.putMethod(new StringValue(methodName), (interpreter, args) -> {
			throw new NotImplemented(methodName);
		});
	}

	// https://tc39.es/ecma262/multipage#sec-math.hypot + sec-math.min + sec-math.max
	private void addWrapper(String methodName, DoubleRestArgs restArgs) {
		this.putMethod(new StringValue(methodName), (interpreter, args) -> {
			final double[] coerced = new double[args.length];
			for (int i = 0; i < args.length; i++) {
				coerced[i] = args[i].toNumberValue(interpreter).value;
			}

			return new NumberValue(restArgs.applyAsDouble(coerced));
		});
	}

	private void addWrapper(String methodName, DoubleUnaryOperator unaryOperator) {
		this.putMethod(new StringValue(methodName), (interpreter, args) -> {
			final var number = argumentDouble(0, interpreter, args);
			return new NumberValue(unaryOperator.applyAsDouble(number));
		});
	}

	private void addWrapper(String methodName, DoubleBinaryOperator binaryOperator) {
		this.putMethod(new StringValue(methodName), (interpreter, args) -> {
			final var a = argumentDouble(0, interpreter, args);
			final var b = argumentDouble(1, interpreter, args);
			return new NumberValue(binaryOperator.applyAsDouble(a, b));
		});
	}

	@FunctionalInterface
	private interface DoubleRestArgs {
		double applyAsDouble(double[] coerced);
	}
}