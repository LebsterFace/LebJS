package xyz.lebster.core.value.primitive.string;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.range.RangeError;
import xyz.lebster.core.value.primitive.PrimitiveConstructor;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string-constructor")
@NonStandard
public class StringConstructor extends PrimitiveConstructor {
	public StringConstructor(Intrinsics intrinsics) {
		super(intrinsics, Names.String);
		putMethod(intrinsics, Names.fromCharCode, 1, StringConstructor::fromCharCode);
		putMethod(intrinsics, Names.fromCodePoint, 1, StringConstructor::fromCodePoint);
		putMethod(intrinsics, Names.raw, 1, StringConstructor::raw);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.raw")
	private static StringValue raw(Interpreter interpreter, Value<?>[] arguments) {
		// 22.1.2.4 String.raw ( template, ...substitutions )
		throw new NotImplemented("String.raw");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.fromcodepoint")
	private static StringValue fromCodePoint(Interpreter interpreter, Value<?>[] codePoints) throws AbruptCompletion {
		// 22.1.2.2 String.fromCodePoint ( ...codePoints )

		// 1. Let result be the empty String.
		final StringBuilder result = new StringBuilder();
		// 2. For each element next of codePoints, do
		for (final Value<?> next : codePoints) {
			// a. Let nextCP be ? ToNumber(next).
			final NumberValue nextCP = next.toNumberValue(interpreter);
			// b. If nextCP is not an integral Number, throw a RangeError exception.
			// c. If ℝ(nextCP) < 0 or ℝ(nextCP) > 0x10FFFF, throw a RangeError exception.
			if (!nextCP.isIntegralNumber() || nextCP.value < 0 || nextCP.value > 0x10FFFF)
				throw error(new RangeError(interpreter, "Invalid code point " + nextCP.toDisplayString(true)));
			// d. Set result to the string-concatenation of result and UTF16EncodeCodePoint(ℝ(nextCP)).
			result.appendCodePoint(nextCP.value.intValue());
		}

		// 3. Assert: If codePoints is empty, then result is the empty String.
		// 4. Return result.
		return new StringValue(result.toString());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string.fromcharcode")
	private static StringValue fromCharCode(Interpreter interpreter, Value<?>[] codeUnits) throws AbruptCompletion {
		// 22.1.2.1 String.fromCharCode ( ...codeUnits )

		// 1. Let result be the empty String.
		final StringBuilder result = new StringBuilder();
		// 2. For each element next of codeUnits, do
		for (final Value<?> next : codeUnits) {
			// a. Let nextCU be the code unit whose numeric value is ℝ(? ToUint16(next)).
			final char nextCU = (char) next.toNumberValue(interpreter).toUint16();
			// b. Set result to the string-concatenation of result and nextCU.
			result.append(nextCU);
		}

		// 3. Return result.
		return new StringValue(result.toString());
	}

	@Override
	public StringValue internalCall(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		if (arguments.length == 0) return StringValue.EMPTY;
		else if (arguments[0] instanceof SymbolValue symbolValue) return new StringValue(symbolValue.symbolDescriptiveString());
		else return arguments[0].toStringValue(interpreter);
	}
}
