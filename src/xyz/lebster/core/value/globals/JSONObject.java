package xyz.lebster.core.value.globals;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.StringEscapeUtils;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.node.expression.ArrayExpression;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.node.expression.ObjectExpression;
import xyz.lebster.core.parser.Parser;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.runtimesyntax.SyntaxErrorObject;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.string.StringValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.array.ArrayPrototype.isArray;
import static xyz.lebster.core.value.array.ArrayPrototype.lengthOfArrayLike;
import static xyz.lebster.core.value.function.NativeFunction.argument;

public final class JSONObject extends ObjectValue {
	public JSONObject(Intrinsics intrinsics) {
		super(intrinsics);
		putMethod(intrinsics, Names.parse, 2, JSONObject::parse);
		putMethod(intrinsics, Names.stringify, 3, JSONObject::stringify);
		put(SymbolValue.toStringTag, Names.JSON, false, false, true);
	}

	@NonStandard
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-json.parse")
	private static Value<?> parse(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 25.5.1 JSON.parse ( text [ , reviver ] )
		final Value<?> text = argument(0, arguments);
		final Value<?> reviver_ = argument(1, arguments);
		// NOTE: Steps 5 and 8 (notes) are not relevant as __proto__ has been excluded from LebJS.

		// 1. Let jsonString be ? ToString(text).
		final StringValue jsonString = text.toStringValue(interpreter);
		// 2. Parse StringToCodePoints(jsonString) as a JSON text as specified in ECMA-404.
		// Throw a SyntaxError exception if it is not a valid JSON text as defined in that specification.
		try {
			new JSONParser(jsonString.value).parse();
		} catch (JSONParser.JSONParseError e) {
			throw error(new SyntaxErrorObject(interpreter, e.getMessage()));
		}
		// 3. Let scriptString be the string-concatenation of "(", jsonString, and ");".
		// 4. Let script be ParseText(StringToCodePoints(scriptString), Script).
		// 6. Assert: script is a Parse Node.
		// 7. Let completion be Completion(Evaluation of script).
		// 9. Let unfiltered be completion.[[Value]].
		// NOTE: This should be equivalent:
		final Expression expression;
		try {
			final Parser parser = new Parser(jsonString.value.trim());
			expression = parser.parseExpression();
		} catch (SyntaxError e) {
			throw new ShouldNotHappen("SyntaxError while parsing JSON");
		}

		final Value<?> unfiltered;
		try {
			unfiltered = expression.execute(interpreter);
		} catch (AbruptCompletion e) {
			throw new ShouldNotHappen("AbruptCompletion while evaluating JSON");
		}

		// 10. Assert: unfiltered is either a String, a Number, a Boolean,
		// an Object that is defined by either an ArrayLiteral or an ObjectLiteral, or null.
		if (!(
			unfiltered instanceof StringValue
			|| unfiltered instanceof NumberValue
			|| unfiltered instanceof BooleanValue
			|| unfiltered == Null.instance
			|| (unfiltered instanceof ObjectValue && expression instanceof ObjectExpression)
			|| (unfiltered instanceof ObjectValue && expression instanceof ArrayExpression)
		)) {
			throw new ShouldNotHappen("JSON evaluated to illegal value");
		}

		// 11. If IsCallable(reviver) is true, then
		if (reviver_ instanceof final Executable reviver) {
			// TODO: a. Let root be OrdinaryObjectCreate(%Object.prototype%).
			final ObjectValue root = new ObjectValue(interpreter.intrinsics);
			// b. Let rootName be the empty String.
			final StringValue rootName = StringValue.EMPTY;
			// FIXME: c. Perform ! CreateDataPropertyOrThrow(root, rootName, unfiltered).
			root.put(rootName, unfiltered);
			// d. Return ? InternalizeJSONProperty(root, rootName, reviver).
			return internalizeJSONProperty(interpreter, root, rootName, reviver);
		}
		// 12. Else,
		else {
			// a. Return unfiltered.
			return unfiltered;
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-internalizejsonproperty")
	private static Value<?> internalizeJSONProperty(Interpreter interpreter, ObjectValue holder, StringValue name, Executable reviver) throws AbruptCompletion {
		// TODO: This algorithm intentionally does not throw an exception if either [[Delete]] or CreateDataProperty return false.

		// 1. Let val be ? Get(holder, name).
		final Value<?> val = holder.get(interpreter, name);
		// 2. If val is an Object, then
		if (val instanceof final ObjectValue obj) {
			// a. Let isArray be ? IsArray(val).
			final boolean isArray = isArray(val);
			// b. If isArray is true, then
			if (isArray) {
				// i. Let len be ? LengthOfArrayLike(val).
				final int len = lengthOfArrayLike(interpreter, obj);
				// ii. Let I be 0.
				// iii. Repeat, while I < len,
				for (int I = 0; I < len; I++) {
					// 1. Let prop be ! ToString(𝔽(I)).
					final StringValue prop = new StringValue(I);
					// 2. Let newElement be ? InternalizeJSONProperty(val, prop, reviver).
					final Value<?> newElement = internalizeJSONProperty(interpreter, obj, prop, reviver);
					// 3. If newElement is undefined, then
					if (newElement == Undefined.instance) {
						// a. Perform ? val.[[Delete]](prop).
						obj.delete(prop);
					}
					// 4. Else,
					else {
						// TODO: a. Perform ? CreateDataProperty(val, prop, newElement).
						obj.put(prop, newElement);
					}
					// 5. Set I to I + 1.
				}
			}
			// c. Else,
			else {
				// TODO: i. Let keys be ? EnumerableOwnProperties(val, key).
				final var keys = obj.enumerableOwnPropertyNames(interpreter, true, false);
				// ii. For each String P of keys, do
				for (final Value<?> V : keys) {
					// TODO: Enforce this through types
					if (!(V instanceof final StringValue P)) throw new ShouldNotHappen("Non-string key returned");
					// 1. Let newElement be ? InternalizeJSONProperty(val, P, reviver).
					final Value<?> newElement = internalizeJSONProperty(interpreter, obj, P, reviver);
					// 2. If newElement is undefined, then
					if (newElement == Undefined.instance) {
						// a. Perform ? val.[[Delete]](P).
						obj.delete(P);
					}
					// 3. Else,
					else {
						// FIXME: a. Perform ? CreateDataProperty(val, P, newElement).
						obj.put(P, newElement);
					}
				}
			}
		}

		// 3. Return ? Call(reviver, holder, « name, val »).
		return reviver.call(interpreter, holder, name, val);
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-json.stringify")
	private static StringValue stringify(Interpreter interpreter, Value<?>[] arguments) {
		// 25.5.2 JSON.stringify ( value [ , replacer [ , space ] ] )
		final Value<?> value = argument(0, arguments);
		final Value<?> replacer = argument(1, arguments);
		final Value<?> space = argument(2, arguments);

		throw new NotImplemented("JSON.stringify");
	}

	// TODO: More specific parsing errors
	@SpecificationURL("https://www.ecma-international.org/wp-content/uploads/ECMA-404_2nd_edition_december_2017.pdf")
	private static final class JSONParser {
		private int index = 0;
		private final String sourceText;
		private final int[] codepoints;

		private static final class JSONParseError extends Exception {
			public JSONParseError(String message) {
				super(message);
			}
		}

		public JSONParser(String sourceText) {
			this.sourceText = sourceText;
			this.codepoints = sourceText.codePoints().toArray();
		}

		public void parse() throws JSONParseError {
			whitespace();
			parseValue();
			if (index < codepoints.length)
				throw new JSONParseError("Unexpected non-whitespace character " + quoteCurrent() + " after JSON");
		}

		private JSONParseError unexpected() {
			if (outOfBounds()) return new JSONParseError("Unexpected end of JSON input");
			return new JSONParseError("Unexpected token " + quoteCurrent() + " in JSON");
		}

		private boolean outOfBounds() {
			return index >= codepoints.length;
		}

		public void parseValue() throws JSONParseError {
			if (is('{')) parseObject();
			else if (is('[')) parseArray();
			else if (is('-') || isDigit()) parseNumber();
			else if (is('"')) parseString();
			else if (optional("true") || optional("false") || optional("null")) whitespace();
			else throw unexpected();
		}

		private void parseEscapeSequence() throws JSONParseError {
			if (outOfBounds()) throw new JSONParseError("Unexpected end of JSON input");
			if (optional('u')) {
				// Four hex digits
				for (int i = 0; i < 4; i++) {
					if (isHexDigit()) {
						index++;
					} else {
						throw new JSONParseError("Bad Unicode escape in JSON");
					}
				}
			} else if (!optional('"')
				 && !optional('\\')
				 && !optional('/')
				 && !optional('b')
				 && !optional('f')
				 && !optional('n')
				 && !optional('r')
				 && !optional('t')
			) {
				throw new JSONParseError("Bad escaped character in JSON");
			}
		}

		private void parseString() throws JSONParseError {
			require('"');
			while (true) {
				if (outOfBounds())
					throw new JSONParseError("Unterminated string in JSON");

				if (optional('"')) {
					whitespace();
					break;
				}

				if (codepoints[index] <= 0x001F) {
					throw new JSONParseError("Bad control character " + quoteCurrent() + " in string literal in JSON");
				} else if (optional('\\')) {
					parseEscapeSequence();
				} else {
					index++;
				}
			}
		}

		private void parseNumber() throws JSONParseError {
			optional('-');
			if (!isDigit()) throw new JSONParseError("No number after minus sign in JSON");
			// If the first digit is zero, there must only be one.
			if (!optional('0')) consumeDigits();

			if (optional('.')) {
				if (!isDigit()) throw new JSONParseError("Unterminated fractional number in JSON");
				else consumeDigits();
			}

			if (optional('e') || optional('E')) {
				if (is('+') || is('-')) index++;
				if (!isDigit()) throw new JSONParseError("Exponent part is missing a number in JSON");
				consumeDigits();
			}

			whitespace();
		}

		private void consumeDigits() {
			while (isDigit()) {
				index++;
			}
		}

		private void parseArray() throws JSONParseError {
			require('[');
			whitespace();

			if (optional(']')) {
				whitespace();
				return;
			}

			while (true) {
				parseValue();
				if (optional(']')) {
					whitespace();
					return;
				}

				require(',');
				whitespace();
			}
		}

		private void parseObject() throws JSONParseError {
			require('{');
			whitespace();

			if (optional('}')) {
				whitespace();
				return;
			}

			while (true) {
				parseString();
				require(':');
				whitespace();
				parseValue();
				if (optional('}')) {
					whitespace();
					return;
				}
				require(',');
				whitespace();
			}
		}

		private String quoteCurrent() {
			return StringEscapeUtils.quote(Character.toString(codepoints[index]), false);
		}

		private void whitespace() {
			while (index < codepoints.length && isWhitespace()) {
				index++;
			}
		}

		private boolean isWhitespace() {
			return '\t' == codepoints[index] ||
				   '\n' == codepoints[index] ||
				   '\r' == codepoints[index] ||
				   ' ' == codepoints[index];
		}

		private boolean is(int codepoint) {
			if (outOfBounds()) return false;
			return codepoints[index] == codepoint;
		}

		private boolean isDigit() {
			if (outOfBounds()) return false;
			return codepoints[index] >= (int) '0' && codepoints[index] <= (int) '9';
		}

		private boolean isHexDigit() {
			if (outOfBounds()) return false;
			return (codepoints[index] >= (int) '0' && codepoints[index] <= (int) '9') || (codepoints[index] >= 'A' && codepoints[index] <= 'F') || (codepoints[index] >= 'a' && codepoints[index] <= 'f');
		}

		private boolean optional(int codepoint) {
			if (is(codepoint)) {
				index++;
				return true;
			}

			return false;
		}

		private boolean optional(String string) {
			for (int I : string.codePoints().toArray()) {
				if (!is(I)) return false;
				index++;
			}

			return true;
		}

		private void require(int codepoint) throws JSONParseError {
			if (!is(codepoint)) throw unexpected();
			index++;
		}
	}
}
