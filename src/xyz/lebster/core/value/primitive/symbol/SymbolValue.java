package xyz.lebster.core.value.primitive.symbol;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.object.Key;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.bigint.BigIntValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ecmascript-language-types-symbol-type")
@NonStandard
public final class SymbolValue extends Key<Void> {
	// https://tc39.es/ecma262/multipage#table-well-known-symbols

	/**
	 * A method that returns the default AsyncIterator for an object.
	 * Called by the semantics of the for-await-of statement.
	 */
	public static final SymbolValue asyncIterator = new SymbolValue(Names.SymbolDotAsyncIterator);
	/**
	 * A method that determines if a constructor object recognizes an object as one of the constructor's instances.
	 * Called by the semantics of the instanceof operator.
	 */
	public static final SymbolValue hasInstance = new SymbolValue(Names.SymbolDotHasInstance);
	/**
	 * A Boolean valued property that if true indicates that an object should be flattened to
	 * its array elements by Array.prototype.concat.
	 */
	public static final SymbolValue isConcatSpreadable = new SymbolValue(Names.SymbolDotIsConcatSpreadable);
	/**
	 * A method that returns the default Iterator for an object. Called by the semantics of the for-of statement.
	 */
	public static final SymbolValue iterator = new SymbolValue(Names.SymbolDotIterator);
	/**
	 * A regular expression method that matches the regular expression against a string.
	 * Called by the String.prototype.match method.
	 */
	public static final SymbolValue match = new SymbolValue(Names.SymbolDotMatch);
	/**
	 * A regular expression method that returns an iterator, that yields matches of the regular expression against a
	 * string. Called by the String.prototype.matchAll method.
	 */
	public static final SymbolValue matchAll = new SymbolValue(Names.SymbolDotMatchAll);
	/**
	 * A regular expression method that replaces matched substrings of a string.
	 * Called by the String.prototype.replace method.
	 */
	public static final SymbolValue replace = new SymbolValue(Names.SymbolDotReplace);
	/**
	 * A regular expression method that returns the index within a string that matches the regular expression.
	 * Called by the String.prototype.search method.
	 */
	public static final SymbolValue search = new SymbolValue(Names.SymbolDotSearch);
	/**
	 * A regular expression method that splits a string at the indices that match the regular expression.
	 * Called by the String.prototype.split method.
	 */
	public static final SymbolValue split = new SymbolValue(Names.SymbolDotSplit);
	/**
	 * A method that converts an object to a corresponding primitive value.
	 * Called by the ToPrimitive abstract operation.
	 */
	public static final SymbolValue toPrimitive = new SymbolValue(Names.SymbolDotToPrimitive);
	/**
	 * A String valued property that is used in the creation of the default string description of an object.
	 * Accessed by the built-in method Object.prototype.toString.
	 */
	public static final SymbolValue toStringTag = new SymbolValue(Names.SymbolDotToStringTag);

	private static int LAST_UNUSED_IDENTIFIER = 0;
	public final StringValue description;
	private final int UNIQUE_ID = LAST_UNUSED_IDENTIFIER++;

	public SymbolValue(StringValue description) {
		super(null);
		this.description = description;
	}

	@Override
	public StringValue toFunctionName() {
		return new StringValue('[' + description.value + ']');
	}

	@Override
	public int toIndex() {
		return -1;
	}

	@Override
	public boolean equalsKey(Key<?> other) {
		return other == this;
	}

	@Override
	public StringValue toStringValue(Interpreter interpreter) throws AbruptCompletion {
		throw error(new TypeError(interpreter, "Cannot convert a Symbol value to a string"));
	}

	@Override
	public NumberValue toNumberValue(Interpreter interpreter) throws AbruptCompletion {
		throw error(new TypeError(interpreter, "Cannot convert a Symbol value to a number"));
	}

	@Override
	public BooleanValue toBooleanValue(Interpreter interpreter) {
		return BooleanValue.TRUE;
	}

	@Override
	public ObjectValue toObjectValue(Interpreter interpreter) {
		return new SymbolWrapper(interpreter.intrinsics, this);
	}

	@Override
	public BigIntValue toBigIntValue(Interpreter interpreter) throws AbruptCompletion {
		throw error(new TypeError(interpreter, "Cannot convert %s to a BigInt".formatted(symbolDescriptiveString())));
	}

	@Override
	public String typeOf() {
		return "symbol";
	}

	@Override
	public void display(StringRepresentation representation) {
		representation.append(this.symbolDescriptiveString());
	}

	public String symbolDescriptiveString() {
		return "Symbol(" + (description == null ? "" : description.value) + ")";
	}

	@Override
	public boolean equals(Object o) {
		return this == o;
	}

	@Override
	public int hashCode() {
		return UNIQUE_ID;
	}
}
