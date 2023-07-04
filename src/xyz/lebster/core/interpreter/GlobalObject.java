package xyz.lebster.core.interpreter;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.interpreter.environment.ExecutionContext;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.EvalError;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.Path;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.function.NativeFunction.argument;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-global-object")
public final class GlobalObject extends ObjectValue {
	public GlobalObject(Intrinsics intrinsics) {
		super(intrinsics);

		// 19.1 Value Properties of the Global Object
		put(Names.globalThis, this);
		put(Names.NaN, NumberValue.NaN, false, false, false);
		put(Names.Infinity, new NumberValue(Double.POSITIVE_INFINITY), false, false, false);
		put(Names.undefined, Undefined.instance, false, false, false);

		// 19.2 Function Properties of the Global Object
		putMethod(intrinsics, Names.eval, 1, GlobalObject::eval);
		putMethod(intrinsics, Names.isFinite, 1, GlobalObject::isFinite);
		putMethod(intrinsics, Names.isNaN, 1, GlobalObject::isNaN);
		putMethod(intrinsics, Names.parseFloat, 1, GlobalObject::parseFloat);
		putMethod(intrinsics, Names.parseInt, 2, GlobalObject::parseInt);

		// 19.3 Constructor Properties of the Global Object
		put(Names.Array, intrinsics.arrayConstructor);
		put(Names.Boolean, intrinsics.booleanConstructor);
		put(Names.Error, intrinsics.errorConstructor);
		put(Names.Function, intrinsics.functionConstructor);
		put(Names.Map, intrinsics.mapConstructor);
		put(Names.Number, intrinsics.numberConstructor);
		put(Names.Object, intrinsics.objectConstructor);
		put(Names.RangeError, intrinsics.rangeErrorConstructor);
		put(Names.ReferenceError, intrinsics.referenceErrorConstructor);
		put(Names.RegExp, intrinsics.regExpConstructor);
		put(Names.Set, intrinsics.setConstructor);
		put(Names.String, intrinsics.stringConstructor);
		put(Names.Symbol, intrinsics.symbolConstructor);
		put(Names.TypeError, intrinsics.typeErrorConstructor);

		// 19.4 Other Properties of the Global Object
		put(Names.Math, intrinsics.mathObject);

		// Non-Standard properties
		put(Names.Test, intrinsics.testObject);
		put(Names.console, intrinsics.consoleObject);
		put(Names.ShadowRealm, intrinsics.shadowRealmConstructor);
		putMethod(intrinsics, Names.readFile, 2, GlobalObject::readFile);
		putMethod(intrinsics, Names.cwd, 2, GlobalObject::cwd);
		putMethod(intrinsics, Names.isStrictMode, 0, GlobalObject::isStrictMode);
	}

	private static BooleanValue isStrictMode(Interpreter interpreter, Value<?>[] arguments) {
		return BooleanValue.of(interpreter.isStrictMode());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-parseint-string-radix")
	private static NumberValue parseInt(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 19.2.5 parseInt ( string, radix )
		final Value<?> string = argument(0, arguments);
		final Value<?> radix = argument(1, arguments);

		if (arguments.length == 0) return NumberValue.NaN;

		// 1. Let inputString be ? ToString(string).
		final String inputString = string.toStringValue(interpreter).value;
		// 2. Let S be ! TrimString(inputString, start).
		final StringBuilder S = new StringBuilder(inputString.stripLeading());
		// 3. Let sign be 1.
		int sign = 1;
		// 4. If S is not empty and the first code unit of S is the code unit 0x002D (HYPHEN-MINUS), set sign to -1.
		if (!S.isEmpty() && S.charAt(0) == 0x002D) sign = -1;
		// 5. If S is not empty and the first code unit of S is the code unit 0x002B (PLUS SIGN) or the code unit 0x002D (HYPHEN-MINUS)
		if (!S.isEmpty() && (S.charAt(0) == 0x002B || S.charAt(0) == 0x002D))
			// remove the first code unit from S.
			S.deleteCharAt(0);
		// 6. Let R be ℝ(? ToInt32(radix)).
		int R = radix.toNumberValue(interpreter).toInt32();
		// 7. Let stripPrefix be true.
		boolean stripPrefix = true;
		// 8. If R ≠ 0, then
		if (R != 0) {
			// a. If R < 2 or R > 36, return NaN.
			if (R < 2 || R > 36) return NumberValue.NaN;
			// b. If R ≠ 16, set stripPrefix to false.
			if (R != 16) stripPrefix = false;
		} else {
			// 9. Else, a. Set R to 10.
			R = 10;
		}

		// 10. If stripPrefix is true, then
		if (stripPrefix) {
			// a. If the length of S is at least 2 and the first two code units of S are either "0x" or "0X", then
			if (S.length() >= 2 && S.charAt(0) == '0' && (S.charAt(1) == 'X' || S.charAt(1) == 'x')) {
				// i. Remove the first two code units from S.
				S.delete(0, 2);
				// ii. Set R to 16.
				R = 16;
			}
		}

		// 11. If S contains a code unit that is not a radix-R digit, let end be the index within S of the first
		// such code unit; otherwise, let end be the length of S.
		int end = 0;
		while (end < S.length()) {
			if (Character.digit(S.charAt(end), R) == -1) break;
			end++;
		}

		// 12. Let Z be the substring of S from 0 to end.
		final String Z = S.substring(0, end);
		// 13. If Z is empty, return NaN.
		if (Z.isEmpty()) return NumberValue.NaN;

		// 14. Let mathInt be the integer value that is represented by Z in radix-R notation, using the letters
		// A-Z and a-z for digits with values 10 through 35. (However, if R is 10 and Z contains more than 20
		// significant digits, every significant digit after the 20th may be replaced by a 0 digit, at the option
		// of the implementation; and if R is not 2, 4, 8, 10, 16, or 32, then mathInt may be an
		// implementation-approximated integer representing the integer value denoted by Z in radix-R notation.)
		int mathInt = Integer.parseInt(Z, R);

		// 15. If mathInt = 0, then
		if (mathInt == 0) {
			// a. If sign = -1, return -0𝔽.
			// b. Return +0𝔽.
			return new NumberValue(0.0D * sign);
		}

		// 16. Return 𝔽(sign × mathInt).
		return new NumberValue(mathInt * sign);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-parsefloat-string")
	private static NumberValue parseFloat(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 19.2.4 parseFloat ( string )
		final Value<?> string = argument(0, arguments);
		if (arguments.length == 0) return NumberValue.NaN;

		// 1. Let inputString be ? ToString(string).
		final StringValue inputString = string.toStringValue(interpreter);
		// 2. Let trimmedString be ! TrimString(inputString, start).
		final String trimmedString = inputString.value.stripLeading();
		// FIXME: Follow spec
		return new NumberValue(Double.parseDouble(trimmedString));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-isfinite-number")
	private static BooleanValue isFinite(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 19.2.2 isFinite ( number )
		final Value<?> number = argument(0, arguments);
		if (arguments.length == 0) return BooleanValue.FALSE;

		// 1. Let num be ? ToNumber(number).
		final double num = number.toNumberValue(interpreter).value;
		// 2. If num is NaN, +∞𝔽, or -∞𝔽, return false.
		// 3. Otherwise, return true.
		return BooleanValue.of(!(Double.isNaN(num) || Double.isInfinite(num)));
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-eval-x")
	private static Value<?> eval(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 19.2.1 eval ( x )
		final Value<?> x = argument(0, arguments);
		if (arguments.length == 0) return Undefined.instance;

		final String sourceText = x.toStringValue(interpreter).value;
		final ExecutionContext context = interpreter.pushContextWithNewEnvironment();
		try {
			return Realm.executeWith(sourceText, interpreter);
		} catch (SyntaxError e) {
			throw error(new EvalError(interpreter, e));
		} finally {
			interpreter.exitExecutionContext(context);
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-isnan-number")
	private static BooleanValue isNaN(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 19.2.3 isNaN ( number )
		final Value<?> number = argument(0, arguments);

		// 1. Let num be ? ToNumber(number).
		final NumberValue num = number.toNumberValue(interpreter);
		// 2. If num is NaN, return true.
		// 3. Otherwise, return false.
		return BooleanValue.of(num.value.isNaN());
	}

	private static StringValue readFile(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// readFile(path: string, encoding: Charset): string
		if (arguments.length != 2) throw error(new TypeError(interpreter, "readFile requires 2 arguments: path and charset"));
		final Value<?> pathArgument = argument(0, arguments);
		if (!(pathArgument instanceof final StringValue path))
			throw error(new TypeError(interpreter, pathArgument.toStringValue(interpreter).value + " is not a string"));
		final Value<?> charsetArgument = argument(1, arguments);
		if (!(charsetArgument instanceof final StringValue charsetString))
			throw error(new TypeError(interpreter, charsetArgument.toStringValue(interpreter).value + " is not a string"));

		Charset charset;
		try {
			charset = Charset.forName(charsetString.value);
		} catch (IllegalCharsetNameException e) {
			throw error(new TypeError(interpreter, "Illegal charset name: " + charsetArgument.toStringValue(interpreter).value));
		} catch (UnsupportedCharsetException e) {
			throw error(new TypeError(interpreter, "Unsupported charset: " + e.getCharsetName()));
		}

		try {
			final String s = Files.readString(Path.of(path.value), charset);
			return new StringValue(s);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static StringValue cwd(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// cwd(): string
		if (arguments.length != 0) {
			throw error(new TypeError(interpreter, "cwd() called with >0 arguments"));
		} else {
			return new StringValue(System.getProperty("user.dir"));
		}
	}
}