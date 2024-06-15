package xyz.lebster.core.interpreter;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.environment.ExecutionContext;
import xyz.lebster.core.parser.Lexer;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.math.BigInteger;

import static xyz.lebster.core.value.function.NativeFunction.argument;

@NonStandard
@SpecificationURL("https://tc39.es/ecma262/multipage#sec-global-object")
public final class GlobalObject extends ObjectValue {
	public GlobalObject(Intrinsics intrinsics) {
		super(intrinsics);

		// 19.1 Value Properties of the Global Object
		put(Names.Infinity, NumberValue.POSITIVE_INFINITY, false, false, false);
		put(Names.NaN, NumberValue.NaN, false, false, false);
		put(Names.globalThis, this);
		put(Names.undefined, Undefined.instance, false, false, false);

		// 19.2 Function Properties of the Global Object
		putMethod(intrinsics, Names.eval, 1, GlobalObject::eval);
		putMethod(intrinsics, Names.parseFloat, 1, GlobalObject::parseFloat);
		putMethod(intrinsics, Names.parseInt, 2, GlobalObject::parseInt);

		// 19.3 Constructor Properties of the Global Object
		put(Names.Array, intrinsics.arrayConstructor);
		put(Names.BigInt, intrinsics.bigIntConstructor);
		put(Names.Boolean, intrinsics.booleanConstructor);
		put(Names.Error, intrinsics.errorConstructor);
		put(Names.Function, intrinsics.functionConstructor);
		put(Names.Iterator, intrinsics.iteratorConstructor);
		put(Names.Map, intrinsics.mapConstructor);
		put(Names.Number, intrinsics.numberConstructor);
		put(Names.Object, intrinsics.objectConstructor);
		put(Names.RangeError, intrinsics.rangeErrorConstructor);
		put(Names.ReferenceError, intrinsics.referenceErrorConstructor);
		put(Names.RegExp, intrinsics.regExpConstructor);
		put(Names.Set, intrinsics.setConstructor);
		put(Names.String, intrinsics.stringConstructor);
		put(Names.Symbol, intrinsics.symbolConstructor);
		put(Names.SyntaxError, intrinsics.syntaxErrorConstructor);
		put(Names.TypeError, intrinsics.typeErrorConstructor);

		// 19.4 Other Properties of the Global Object
		put(Names.JSON, intrinsics.jsonObject);
		put(Names.Math, intrinsics.mathObject);

		// Non-Standard properties
		put(Names.ShadowRealm, intrinsics.shadowRealmConstructor);
		put(Names.Test, intrinsics.testObject);
		put(Names.fs, intrinsics.fileSystemObject);
		put(Names.console, intrinsics.consoleObject);
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

		// 1. Let inputString be ? ToString(string).
		final String inputString = string.toStringValue(interpreter).value;
		// 2. Let S be ! TrimString(inputString, start).
		String S = inputString.stripLeading();
		// 3. Let sign be 1.
		// 4. If S is not empty and the first code unit of S is the code unit 0x002D (HYPHEN-MINUS), set sign to -1.
		final boolean isPositive = !S.startsWith("-");
		// 5. If S is not empty and the first code unit of S is either the code unit 0x002B (PLUS SIGN) or the code unit 0x002D (HYPHEN-MINUS),
		if (S.startsWith("+") || S.startsWith("-")) {
			// set S to the substring of S from index 1.
			S = S.substring(1);
		}
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
		}
		// 9. Else,
		else {
			// a. Set R to 10.
			R = 10;
		}
		// 10. If stripPrefix is true, then
		if (stripPrefix) {
			// a. If the length of S is at least 2 and the first two code units of S are either "0x" or "0X", then
			if (S.startsWith("0x") || S.startsWith("0X")) {
				// i. Set S to the substring of S from index 2.
				S = S.substring(2);
				// ii. Set R to 16.
				R = 16;
			}
		}

		// 11. If S contains a code unit that is not a radix-R digit,
		// let end be the index within S of the first such code unit; otherwise, let end be the length of S.
		int end = 0;
		while (end < S.length()) {
			if (!Lexer.isDigit(S.charAt(end), R)) break;
			end++;
		}

		// 12. Let Z be the substring of S from 0 to end.
		final String Z = S.substring(0, end);
		// 13. If Z is empty, return NaN.
		if (Z.isEmpty()) return NumberValue.NaN;

		// 14. Let mathInt be the integer value that is represented by Z in radix-R notation, using the letters
		// A through Z and a through z for digits with values 10 through 35. (However, if R = 10 and Z contains more than 20
		// significant digits, every significant digit after the 20th may be replaced by a 0 digit, at the option
		// of the implementation; and if R is not one of 2, 4, 8, 10, 16, or 32, then mathInt may be an
		// implementation-approximated integer representing the integer value denoted by Z in radix-R notation.)
		final BigInteger mathInt = new BigInteger(Z, R);

		// 15. If mathInt = 0, then
		if (mathInt.signum() == 0) {
			// a. If sign = -1, return -0𝔽.
			if (!isPositive) return NumberValue.NEGATIVE_ZERO;
			// b. Return +0𝔽.
			return NumberValue.ZERO;
		}

		// 16. Return 𝔽(sign × mathInt).
		return new NumberValue(isPositive ? mathInt : mathInt.negate());
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

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-eval-x")
	private static Value<?> eval(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 19.2.1 eval ( x )
		final Value<?> x = argument(0, arguments);
		if (arguments.length == 0) return Undefined.instance;

		final String sourceText = x.toStringValue(interpreter).value;
		final ExecutionContext context = interpreter.pushContextWithNewEnvironment();
		try {
			return interpreter.runtimeParse(sourceText).execute(interpreter);
		} finally {
			interpreter.exitExecutionContext(context);
		}
	}
}