package xyz.lebster.core.value.globals;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.StringEscapeUtils;
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
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.error.syntax.SyntaxErrorObject;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.bigint.BigIntValue;
import xyz.lebster.core.value.primitive.bigint.BigIntWrapper;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanWrapper;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.number.NumberWrapper;
import xyz.lebster.core.value.primitive.string.StringValue;
import xyz.lebster.core.value.primitive.string.StringWrapper;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.array.ArrayPrototype.isArray;
import static xyz.lebster.core.value.array.ArrayPrototype.lengthOfArrayLike;
import static xyz.lebster.core.value.function.NativeFunction.argument;
import static xyz.lebster.core.value.primitive.number.NumberPrototype.toIntegerOrInfinity;
import static xyz.lebster.core.value.primitive.string.StringPrototype.stringPad;

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
						obj.set(interpreter, prop, newElement);
					}
					// 5. Set I to I + 1.
				}
			}
			// c. Else,
			else {
				// i. Let keys be ? EnumerableOwnProperties(val, key).
				final var keys = obj.enumerableOwnProperties(interpreter, true, false);
				// ii. For each String P of keys, do
				for (final Value<?> V : keys) {
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
	private static Value<?> stringify(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 25.5.2 JSON.stringify ( value [ , replacer [ , space ] ] )
		final Value<?> value = argument(0, arguments);
		final Value<?> replacer_ = argument(1, arguments);
		Value<?> space = argument(2, arguments);

		// 1. Let stack be a new empty List.
		final ArrayDeque<ObjectValue> stack = new ArrayDeque<>();
		// 2. Let indent be the empty String.
		String indent = "";
		// 3. Let PropertyList be undefined.
		HashSet<StringValue> propertyList = null;
		// 4. Let ReplacerFunction be undefined.
		Executable replacerFunction = null;
		// 5. If replacer is an Object, then
		if (replacer_ instanceof final ObjectValue replacer) {
			// a. If IsCallable(replacer) is true, then
			if (replacer instanceof final Executable executable) {
				// i. Set ReplacerFunction to replacer.
				replacerFunction = executable;
			}
			// b. Else,
			else {
				// i. Let isArray be ? IsArray(replacer).
				final boolean isArray = isArray(replacer);
				// ii. If isArray is true, then
				if (isArray) {
					// 1. Set PropertyList to a new empty List.
					propertyList = new HashSet<>();
					// 2. Let len be ? LengthOfArrayLike(replacer).
					final int len = lengthOfArrayLike(interpreter, replacer);
					// 3. Let k be 0.
					// 4. Repeat, while k < len,
					for (int k = 0; k < len; k++) {
						// a. Let prop be ! ToString(𝔽(k)).
						final StringValue prop = new StringValue(k);
						// b. Let v be ? Get(replacer, prop).
						final Value<?> v = replacer.get(interpreter, prop);
						// c. Let item be undefined.
						StringValue item = null;
						// d. If v is a String, then
						if (v instanceof final StringValue string) {
							// i. Set item to v.
							item = string;
						}
						// e. Else if v is a Number, then
						else if (v instanceof final NumberValue number) {
							// i. Set item to ! ToString(v).
							item = number.toStringValue(interpreter);
						}
						// f. Else if v is an Object, then if v has a [[StringData]] or [[NumberData]] internal slot,
						else if (v instanceof StringWrapper || v instanceof NumberWrapper) {
							// set item to ? ToString(v).
							item = v.toStringValue(interpreter);
						}

						// g. If item is not undefined and PropertyList does not contain item, then
						if (item != null)
							// i. Append item to PropertyList.
							propertyList.add(item);
						// h. Set k to k + 1.
					}
				}
			}
		}

		// 6. If space is an Object, then
		if (space instanceof ObjectValue) {
			// a. If space has a [[NumberData]] internal slot, then
			if (space instanceof NumberWrapper) {
				// i. Set space to ? ToNumber(space).
				space = space.toNumberValue(interpreter);
			}
			// b. Else if space has a [[StringData]] internal slot, then
			else if (space instanceof StringWrapper) {
				// i. Set space to ? ToString(space).
				space = space.toStringValue(interpreter);
			}
		}

		String gap;
		// 7. If space is a Number, then
		if (space instanceof NumberValue) {
			// a. Let spaceMV be ! ToIntegerOrInfinity(space).
			int spaceMV = toIntegerOrInfinity(interpreter, space);
			// b. Set spaceMV to min(10, spaceMV).
			spaceMV = Math.min(10, spaceMV);
			// c. If spaceMV < 1, let gap be the empty String;
			if (spaceMV < 1) gap = "";
				// otherwise let gap be the String value containing spaceMV occurrences of the code unit 0x0020 (SPACE).
			else gap = " ".repeat(spaceMV);
		}
		// 8. Else if space is a String, then
		else if (space instanceof final StringValue string) {
			// a. If the length of space ≤ 10, let gap be space;
			if (string.value.length() <= 10) gap = string.value;
				// otherwise let gap be the substring of space from 0 to 10.
			else gap = string.value.substring(0, 10);
		}
		// 9. Else,
		else {
			// a. Let gap be the empty String.
			gap = "";
		}

		// TODO: 10. Let wrapper be OrdinaryObjectCreate(%Object.prototype%).
		final ObjectValue wrapper = new ObjectValue(interpreter.intrinsics);
		// TODO: 11. Perform ! CreateDataPropertyOrThrow(wrapper, the empty String, value).
		wrapper.put(Names.EMPTY, value);
		// 12. Let state be the JSON Serialization Record { [[ReplacerFunction]]: ReplacerFunction, [[Stack]]: stack, [[Indent]]: indent, [[Gap]]: gap, [[PropertyList]]: PropertyList }.
		final var state = new JSONSerializationRecord(replacerFunction, stack, indent, gap, propertyList);
		// 13. Return ? SerializeJSONProperty(state, the empty String, wrapper).
		final String result = serializeJSONProperty(interpreter, state, Names.EMPTY, wrapper);
		return result == null ? Undefined.instance : new StringValue(result);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-serializejsonproperty")
	private static String serializeJSONProperty(Interpreter interpreter, JSONSerializationRecord state, StringValue key, ObjectValue holder) throws AbruptCompletion {
		// 1. Let value be ? Get(holder, key).
		Value<?> value = holder.get(interpreter, key);
		// 2. If value is an Object or value is a BigInt, then
		if (value instanceof ObjectValue || value instanceof BigIntValue) {
			// a. Let toJSON be ? GetV(value, "toJSON").
			final Value<?> toJSON = value.toObjectValue(interpreter).get(interpreter, Names.toJSON);
			// b. If IsCallable(toJSON) is true, then
			if (toJSON instanceof final Executable executable) {
				// i. Set value to ? Call(toJSON, value, « key »).
				value = executable.call(interpreter, value, key);
			}
		}

		// 3. If state.[[ReplacerFunction]] is not undefined, then
		if (state.replacerFunction != null) {
			// a. Set value to ? Call(state.[[ReplacerFunction]], holder, « key, value »).
			value = state.replacerFunction.call(interpreter, holder, key, value);
		}

		// 4. If value is an Object, then
		if (value instanceof ObjectValue) {
			// a. If value has a [[NumberData]] internal slot, then
			if (value instanceof NumberWrapper) {
				// i. Set value to ? ToNumber(value).
				value = value.toNumberValue(interpreter);
			}
			// b. Else if value has a [[StringData]] internal slot, then
			else if (value instanceof StringWrapper) {
				// i. Set value to ? ToString(value).
				value = value.toStringValue(interpreter);
			}
			// c. Else if value has a [[BooleanData]] internal slot, then
			else if (value instanceof final BooleanWrapper wrapper) {
				// i. Set value to value.[[BooleanData]].
				value = wrapper.data;
			}
			// d. Else if value has a [[BigIntData]] internal slot, then
			else if (value instanceof final BigIntWrapper wrapper) {
				// i. Set value to value.[[BigIntData]].
				value = wrapper.data;
			}
		}

		// 5. If value is null, return "null".
		if (value == Null.instance) return "null";
		// 6. If value is true, return "true".
		if (value == BooleanValue.TRUE) return "true";
		// 7. If value is false, return "false".
		if (value == BooleanValue.FALSE) return "false";
		// 8. If value is a String, return QuoteJSONString(value).
		if (value instanceof final StringValue string) return quoteJSONString(string.value);
		// 9. If value is a Number, then
		if (value instanceof final NumberValue number) {
			// a. If value is finite, return ! ToString(value).
			if (Double.isFinite(number.value)) return value.toStringValue(interpreter).value;
			// b. Return "null".
			return "null";
		}

		// 10. If value is a BigInt, throw a TypeError exception.
		if (value instanceof BigIntValue)
			throw error(new TypeError(interpreter, "Cannot serialize BigInt value to JSON"));

		// 11. If value is an Object and IsCallable(value) is false, then
		if (value instanceof final ObjectValue object && !(value instanceof Executable)) {
			// TODO: a. Let isArray be ? IsArray(value).
			// b. If isArray is true, return ? SerializeJSONArray(state, value).
			if (value instanceof final ArrayObject array) return serializeJSONArray(interpreter, state, array);
			// c. Return ? SerializeJSONObject(state, value).
			return serializeJSONObject(interpreter, state, object);
		}

		// 12. Return undefined.
		return null;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-quotejsonstring")
	private static String quoteJSONString(String value) {
		// 1. Let product be the String value consisting solely of the code unit 0x0022 (QUOTATION MARK).
		final StringBuilder product = new StringBuilder("\"");
		// 2. For each code point C of StringToCodePoints(value), do
		for (var it = value.codePoints().iterator(); it.hasNext(); ) {
			final int C = it.next();
			// a. If C is listed in the “Code Point” column of Table 74, then
			// i. Set product to the string-concatenation of product and the escape sequence for C as specified in the “Escape Sequence” column of the corresponding row.
			product.append(switch (C) {
				case '\b' -> "\\b";
				case '\t' -> "\\t";
				case '\n' -> "\\n";
				case '\f' -> "\\f";
				case '\r' -> "\\r";
				case '"' -> "\\\"";
				case '\\' -> "\\\\";
				// b. Else
				default -> {
					// if C has a numeric value less than 0x0020 (SPACE)
					// or C has the same numeric value as a leading surrogate or trailing surrogate, then
					if (C < 0x020 || C >= 0xD800 && C <= 0xDFFF) {
						// i. Let unit be the code unit whose numeric value is the numeric value of C.
						// ii. Set product to the string-concatenation of product and UnicodeEscape(unit).
						yield unicodeEscape(C);
					}
					// c. Else,
					else {
						// i. Set product to the string-concatenation of product and UTF16EncodeCodePoint(C).
						yield UTF16EncodeCodePoint(C);
					}
				}
			});
		}

		// 3. Set product to the string-concatenation of product and the code unit 0x0022 (QUOTATION MARK).
		product.append('"');
		// 4. Return product.
		return product.toString();
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-unicodeescape")
	private static String unicodeEscape(int n) {
		// 1. Let n be the numeric value of C.
		// 2. Assert: n ≤ 0xFFFF.
		if (n > 0xFFFF) throw new ShouldNotHappen("n > 0xFFFF");
		// 3. Let hex be the String representation of n, formatted as a lowercase hexadecimal number.
		final String hex = Integer.toHexString(n);
		// 4. Return the string-concatenation of the code unit 0x005C (REVERSE SOLIDUS), "u", and StringPad(hex, 4, "0", start).
		return "\\u" + stringPad(hex, 4, "0", true);
	}

	private static int toIntExact(double d) {
		if ((d % 1) != 0) throw new ShouldNotHappen("%f is not an integer.".formatted(d));
		if (d < Integer.MIN_VALUE || d > Integer.MAX_VALUE) throw new ShouldNotHappen("%f is not representable by int.".formatted(d));
		return (int) d;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-utf16encodecodepoint")
	private static String UTF16EncodeCodePoint(int cp) {
		// 1. Assert: 0 ≤ cp ≤ 0x10FFFF.
		if (0 > cp || cp > 0x10FFFF) throw new ShouldNotHappen("Assertion failed.");
		// 2. If cp ≤ 0xFFFF, return the String value consisting of the code unit whose numeric value is cp.
		if (cp <= 0xFFFF) return new String(new int[] { cp }, 0, 1);
		// 3. Let cu1 be the code unit whose numeric value is floor((cp - 0x10000) / 0x400) + 0xD800.
		final int cu1 = toIntExact(Math.floor((double) (cp - 0x10000) / 0x400)) + 0xD800;
		// 4. Let cu2 be the code unit whose numeric value is ((cp - 0x10000) modulo 0x400) + 0xDC00.
		final int cu2 = ((cp - 0x10000) % 0x400) + 0xDC00;
		// 5. Return the string-concatenation of cu1 and cu2.
		return new String(new int[] { cu1, cu2 }, 0, 2);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-serializejsonarray")
	private static String serializeJSONArray(Interpreter interpreter, JSONSerializationRecord state, ArrayObject value) throws AbruptCompletion {
		// 1. If state.[[Stack]] contains value, throw a TypeError exception because the structure is cyclical.
		if (state.stack.contains(value))
			throw error(new TypeError(interpreter, "Cannot stringify circular object"));
		// 2. Append value to state.[[Stack]].
		state.stack.addLast(value);
		// 3. Let stepback be state.[[Indent]].
		final String stepback = state.indent;
		// 4. Set state.[[Indent]] to the string-concatenation of state.[[Indent]] and state.[[Gap]].
		state.indent = state.indent + state.gap;
		// 5. Let partial be a new empty List.
		final ArrayList<String> partial = new ArrayList<>();
		// 6. Let len be ? LengthOfArrayLike(value).
		final int len = lengthOfArrayLike(interpreter, value);
		// 7. Let index be 0.
		// 8. Repeat, while index < len,
		for (int index = 0; index < len; index++) {
			// a. Let strP be ? SerializeJSONProperty(state, ! ToString(𝔽(index)), value).
			final String strP = serializeJSONProperty(interpreter, state, new StringValue(index), value);
			// b. If strP is undefined, then
			// i. Append "null" to partial.
			// c. Else,
			// i. Append strP to partial.
			partial.add(strP == null ? "null" : strP);
			// d. Set index to index + 1.
		}

		String final_;
		// 9. If partial is empty, then
		if (partial.isEmpty()) {
			// a. Let final be "[]".
			final_ = "[]";
		}
		// 10. Else,
		else {
			// a. If state.[[Gap]] is the empty String, then
			if (state.gap.isEmpty()) {
				// i. Let properties be the String value formed by concatenating all the element Strings of partial
				// with each adjacent pair of Strings separated with the code unit 0x002C (COMMA).
				// A comma is not inserted either before the first String or after the last String.
				final String properties = String.join(",", partial);
				// ii. Let final be the string-concatenation of "[", properties, and "]".
				final_ = "[" + properties + "]";
			}
			// b. Else,
			else {
				// i. Let separator be the string-concatenation of the code unit 0x002C (COMMA), the code unit 0x000A (LINE FEED), and state.[[Indent]].
				final String separator = ",\n" + state.indent;
				// ii. Let properties be the String value formed by concatenating all the element Strings of partial
				// with each adjacent pair of Strings separated with separator.
				// The separator String is not inserted either before the first String or after the last String.
				final String properties = String.join(separator, partial);
				// iii. Let final be the string-concatenation of
				// "[", the code unit 0x000A (LINE FEED), state.[[Indent]], properties, the code unit 0x000A (LINE FEED), stepback, and "]".
				final_ = "[\n" + state.indent + properties + "\n" + stepback + "]";
			}
		}

		// 11. Remove the last element of state.[[Stack]].
		state.stack.removeLast();
		// 12. Set state.[[Indent]] to stepback.
		state.indent = stepback;
		// 13. Return final.
		return final_;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-serializejsonobject")
	private static String serializeJSONObject(Interpreter interpreter, JSONSerializationRecord state, ObjectValue value) throws AbruptCompletion {
		// 1. If state.[[Stack]] contains value, throw a TypeError exception because the structure is cyclical.
		if (state.stack.contains(value))
			throw error(new TypeError(interpreter, "Cannot stringify circular object"));
		// 2. Append value to state.[[Stack]].
		state.stack.addLast(value);
		// 3. Let stepback be state.[[Indent]].
		final String stepback = state.indent;
		// 4. Set state.[[Indent]] to the string-concatenation of state.[[Indent]] and state.[[Gap]].
		state.indent += state.gap;
		// 5. If state.[[PropertyList]] is not undefined, then
		final List<StringValue> K;
		if (state.propertyList != null) {
			// a. Let K be state.[[PropertyList]].
			K = state.propertyList;
		}
		// 6. Else,
		else {
			// a. Let K be ? EnumerableOwnProperties(value, key).
			K = new ArrayList<>();
			// TODO: Remove need for this.
			for (final Value<?> property : value.enumerableOwnProperties(interpreter, true, false)) {
				if (property instanceof final StringValue P) {
					K.add(P);
				} else {
					throw new ShouldNotHappen("Non-string key returned");
				}
			}
		}

		// 7. Let partial be a new empty List.
		final ArrayList<String> partial = new ArrayList<>();
		// 8. For each element P of K, do
		for (final StringValue P : K) {
			// a. Let strP be ? SerializeJSONProperty(state, P, value).
			final String strP = serializeJSONProperty(interpreter, state, P, value);
			// b. If strP is not undefined, then
			if (strP != null) {
				// i. Let member be QuoteJSONString(P).
				String member = quoteJSONString(P.value);
				// ii. Set member to the string-concatenation of member and ":".
				member += ":";
				// iii. If state.[[Gap]] is not the empty String, then
				if (!state.gap.isEmpty()) {
					// 1. Set member to the string-concatenation of member and the code unit 0x0020 (SPACE).
					member += " ";
				}

				// iv. Set member to the string-concatenation of member and strP.
				member += strP;
				// v. Append member to partial.
				partial.add(member);
			}
		}

		// 9. If partial is empty, then
		String final_;
		if (partial.isEmpty()) {
			// a. Let final be "{}".
			final_ = "{}";
		}
		// 10. Else,
		else {
			// a. If state.[[Gap]] is the empty String, then
			if (state.gap.isEmpty()) {
				// i. Let properties be the String value formed by concatenating all the element Strings of partial
				// with each adjacent pair of Strings separated with the code unit 0x002C (COMMA).
				// A comma is not inserted either before the first String or after the last String.
				final String properties = String.join(",", partial);
				// ii. Let final be the string-concatenation of "{", properties, and "}".
				final_ = "{" + properties + "}";
			}
			// b. Else,
			else {
				// i. Let separator be the string-concatenation of the code unit 0x002C (COMMA), the code unit 0x000A (LINE FEED), and state.[[Indent]].
				final String separator = ",\n" + state.indent;
				// ii. Let properties be the String value formed by concatenating all the element Strings of partial
				// with each adjacent pair of Strings separated with separator.
				// The separator String is not inserted either before the first String or after the last String.
				final String properties = String.join(separator, partial);
				// iii. Let final be the string-concatenation of
				// "{", the code unit 0x000A (LINE FEED), state.[[Indent]], properties, the code unit 0x000A (LINE FEED), stepback, and "}".
				final_ = "{\n" + state.indent + properties + "\n" + stepback + "}";
			}
		}

		// 11. Remove the last element of state.[[Stack]].
		state.stack.removeLast();
		// 12. Set state.[[Indent]] to stepback.
		state.indent = stepback;
		// 13. Return final.
		return final_;
	}

	private static final class JSONSerializationRecord {
		public final Executable replacerFunction;
		public final ArrayDeque<ObjectValue> stack;
		public String indent;
		public final String gap;
		public final List<StringValue> propertyList;

		public JSONSerializationRecord(Executable replacerFunction, ArrayDeque<ObjectValue> stack, String indent, String gap, HashSet<StringValue> propertyList) {
			this.replacerFunction = replacerFunction;
			this.stack = stack;
			this.indent = indent;
			this.gap = gap;
			this.propertyList = propertyList == null ? null : List.copyOf(propertyList);
		}
	}

	// TODO: More specific parsing errors
	@SpecificationURL("https://www.ecma-international.org/wp-content/uploads/ECMA-404_2nd_edition_december_2017.pdf")
	private static final class JSONParser {
		private final int[] codepoints;
		private int index = 0;

		public JSONParser(String sourceText) {
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

		private static final class JSONParseError extends Exception {
			public JSONParseError(String message) {
				super(message);
			}
		}
	}
}
