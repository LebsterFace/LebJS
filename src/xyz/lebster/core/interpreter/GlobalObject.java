package xyz.lebster.core.interpreter;

import xyz.lebster.cli.Testing;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.CannotParse;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.node.Program;
import xyz.lebster.core.node.value.*;
import xyz.lebster.core.node.value.object.ObjectValue;
import xyz.lebster.core.parser.Lexer;
import xyz.lebster.core.parser.Parser;
import xyz.lebster.core.runtime.ConsoleObject;
import xyz.lebster.core.runtime.EvalError;
import xyz.lebster.core.runtime.MathObject;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-global-object")
public final class GlobalObject extends ObjectValue {
	public GlobalObject() {
		super();

		// 19.1 Value Properties of the Global Object
		put("globalThis", this);

		// FIXME: Property descriptors
		put("NaN", new NativeProperty(new NumberValue(Double.NaN)));
		put("Infinity", new NativeProperty(new NumberValue(Double.POSITIVE_INFINITY)));
		put("undefined", new NativeProperty(Undefined.instance));

		// 19.2 Function Properties of the Global Object
		// FIXME: Follow spec
		setMethod("eval", (interpreter, arguments) -> {
			final String source = arguments.length > 0 ? arguments[0].toStringValue(interpreter).value : "undefined";
			try {
				final Program program = new Parser(new Lexer(source).tokenize()).parse();
				return program.execute(interpreter);
			} catch (CannotParse | SyntaxError e) {
				throw AbruptCompletion.error(new EvalError(e));
			}
		});

		// https://tc39.es/ecma262/multipage#sec-isfinite-number
		setMethod("isFinite", (interpreter, arguments) -> {
			// 1. Let num be ? ToNumber(number).
			final double num = arguments.length > 0 ? arguments[0].toNumberValue(interpreter).value : Double.NaN;
			// 2. If num is NaN, +∞𝔽, or -∞𝔽, return false.
			// 3. Otherwise, return true.
			return BooleanValue.of(!(Double.isNaN(num) || Double.isInfinite(num)));
		});

		// https://tc39.es/ecma262/multipage#sec-isnan-number
		// This method behaves the same as the specification, but does not follow it directly
		setMethod("isNaN", (interpreter, arguments) -> BooleanValue.of(arguments.length == 0 || arguments[0].toNumberValue(interpreter).value.isNaN()));

		// https://tc39.es/ecma262/multipage#sec-parsefloat-string
		setMethod("parseFloat", (interpreter, arguments) -> {
			// 1. Let inputString be ? ToString(string).
			final StringValue string = arguments.length > 0 ? arguments[0].toStringValue(interpreter) : new StringValue("undefined");
			// 2. Let trimmedString be ! TrimString(inputString, start).
			final String trimmedString = string.value.stripLeading();
			// FIXME: Follow spec
			return new NumberValue(Double.parseDouble(trimmedString));
		});

		// https://tc39.es/ecma262/multipage#sec-parseint-string-radix
		setMethod("parseInt", (interpreter, arguments) -> {
			// 1. Let inputString be ? ToString(string).
			final StringValue inputString = arguments.length > 0 ? arguments[0].toStringValue(interpreter) : new StringValue("undefined");
			// 2. Let S be ! TrimString(inputString, start).
			final StringBuilder S = new StringBuilder(inputString.value.stripLeading());
			// 3. Let sign be 1.
			int sign = 1;
			// 4. If S is not empty and the first code unit of S is the code unit 0x002D (HYPHEN-MINUS), set sign to -1.
			if (!S.isEmpty() && S.charAt(0) == 0x002D) sign = -1;
			// 5. If S is not empty and the first code unit of S is the code unit 0x002B (PLUS SIGN) or the code unit 0x002D (HYPHEN-MINUS)
			if (!S.isEmpty() && (S.charAt(0) == 0x002B || S.charAt(0) == 0x002D))
				// remove the first code unit from S.
				S.deleteCharAt(0);
			// 6. Let R be ℝ(? ToInt32(radix)).
			final Value<?> radix = arguments.length > 1 ? arguments[1] : Undefined.instance;
			int R = radix.toNumberValue(interpreter).toInt32();
			// 7. Let stripPrefix be true.
			boolean stripPrefix = true;
			// 8. If R ≠ 0, then
			if (R != 0) {
				// a. If R < 2 or R > 36, return NaN.
				if (R < 2 || R > 36) return new NumberValue(Double.NaN);
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
			if (Z.isEmpty()) return new NumberValue(Double.NaN);

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
		});

		// 19.3 Constructor Properties of the Global Object
		put("Math", MathObject.instance);

		put("console", ConsoleObject.instance);
		Testing.addTestingMethods(this);
	}
}