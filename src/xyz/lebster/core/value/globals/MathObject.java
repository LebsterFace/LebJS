package xyz.lebster.core.value.globals;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.object.ObjectValue;
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
		final FunctionPrototype fp = intrinsics.functionPrototype;

		put(SymbolValue.toStringTag, Names.Math);

		// 21.3.1 Value Properties of the Math Object
		// e, the base of the natural logarithms
		put(Names.E, new NumberValue(2.7182818284590452354D), false, false, false);
		// the natural logarithm of 10
		put(Names.LN10, new NumberValue(2.302585092994046D), false, false, false);
		// the natural logarithm of 2
		put(Names.LN2, new NumberValue(0.6931471805599453D), false, false, false);
		// the base-10 logarithm of e, the base of the natural logarithms
		put(Names.LOG10E, new NumberValue(0.4342944819032518D), false, false, false);
		// the base-2 logarithm of e, the base of the natural logarithms
		put(Names.LOG2E, new NumberValue(1.4426950408889634D), false, false, false);
		// π, the ratio of the circumference of a circle to its diameter
		put(Names.PI, new NumberValue(3.1415926535897932D), false, false, false);
		// the square root of ½
		put(Names.SQRT1_2, new NumberValue(0.7071067811865476D), false, false, false);
		// the square root of 2
		put(Names.SQRT2, new NumberValue(1.4142135623730951D), false, false, false);

		// (double) -> double
		addWrapper(fp, "abs", (DoubleUnaryOperator) Math::abs);
		addWrapper(fp, "acos", Math::acos);
		addWrapper(fp, "asin", Math::asin);
		addWrapper(fp, "atan", Math::atan);
		addWrapper(fp, "ceil", Math::ceil);
		addWrapper(fp, "cbrt", Math::cbrt);
		addWrapper(fp, "expm1", Math::expm1);
		addWrapper(fp, "cos", Math::cos);
		addWrapper(fp, "cosh", Math::cosh);
		addWrapper(fp, "exp", Math::exp);
		addWrapper(fp, "floor", Math::floor);
		addWrapper(fp, "log", Math::log);
		addWrapper(fp, "log1p", Math::log1p);
		addWrapper(fp, "log10", Math::log10);
		addWrapper(fp, "round", (DoubleUnaryOperator) Math::round);
		addWrapper(fp, "sin", Math::sin);
		addWrapper(fp, "sinh", Math::sinh);
		addWrapper(fp, "sqrt", Math::sqrt);
		addWrapper(fp, "tan", Math::tan);
		addWrapper(fp, "tanh", Math::tanh);
		addWrapper(fp, "sign", (DoubleUnaryOperator) Math::signum);

		// (double, double) -> double
		addWrapper(fp, "atan2", Math::atan2);
		addWrapper(fp, "pow", Math::pow);

		// This method conforms to the same interface as the specification, but doesn't
		// follow it exactly. It should behave the same for any given input, however.
		addWrapper(fp, "hypot", (double[] coerced) -> {
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
		addWrapper(fp, "max", (double[] coerced) -> {
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
		addWrapper(fp, "min", (double[] coerced) -> {
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
		this.putMethod(fp, Names.random, (interpreter, args) -> new NumberValue(Math.random()));

		// https://tc39.es/ecma262/multipage#sec-math.trunc
		addWrapper(fp, "trunc", (double n) -> {
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
		addWrapper(fp, "asinh", (double x) -> {
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

		addWrapper(fp, "log2", (double x) -> Math.log(x) / 0.6931471805599453D);
		addWrapper(fp, "acosh", (double x) -> Math.log(x + Math.sqrt(x * x - 1)));
		addWrapper(fp, "atanh", (double x) -> Math.log((1 + x) / (1 - x)) / 2);

		notImplemented(fp, "imul");
		notImplemented(fp, "clz32");
		notImplemented(fp, "fround");
	}

	private void notImplemented(FunctionPrototype fp, String methodName) {
		this.putMethod(fp, new StringValue(methodName), (interpreter, args) -> {
			throw new NotImplemented(methodName);
		});
	}

	// https://tc39.es/ecma262/multipage#sec-math.hypot + sec-math.min + sec-math.max
	private void addWrapper(FunctionPrototype fp, String methodName, DoubleRestArgs restArgs) {
		this.putMethod(fp, new StringValue(methodName), (interpreter, args) -> {
			final double[] coerced = new double[args.length];
			for (int i = 0; i < args.length; i++) {
				coerced[i] = args[i].toNumberValue(interpreter).value;
			}

			return new NumberValue(restArgs.applyAsDouble(coerced));
		});
	}

	private void addWrapper(FunctionPrototype fp, String methodName, DoubleUnaryOperator unaryOperator) {
		this.putMethod(fp, new StringValue(methodName), (interpreter, args) -> {
			final var number = argumentDouble(0, interpreter, args);
			return new NumberValue(unaryOperator.applyAsDouble(number));
		});
	}

	private void addWrapper(FunctionPrototype fp, String methodName, DoubleBinaryOperator binaryOperator) {
		this.putMethod(fp, new StringValue(methodName), (interpreter, args) -> {
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