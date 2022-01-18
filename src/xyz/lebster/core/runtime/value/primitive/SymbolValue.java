package xyz.lebster.core.runtime.value.primitive;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.error.TypeError;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ecmascript-language-types-symbol-type")
public class SymbolValue extends ObjectValue.Key<Void> {
	// https://tc39.es/ecma262/multipage#table-well-known-symbols

	/**
	 * A method that returns the default AsyncIterator for an object.
	 * Called by the semantics of the for-await-of statement.
	 */
	public static final SymbolValue asyncIterator = new SymbolValue("Symbol.asyncIterator");
	/**
	 * A method that determines if a constructor object recognizes an object as one of the constructor's instances.
	 * Called by the semantics of the instanceof operator.
	 */
	public static final SymbolValue hasInstance = new SymbolValue("Symbol.hasInstance");
	/**
	 * A Boolean valued property that if true indicates that an object should be flattened to
	 * its array elements by Array.prototype.concat.
	 */
	public static final SymbolValue isConcatSpreadable = new SymbolValue("Symbol.isConcatSpreadable");
	/**
	 * A method that returns the default Iterator for an object. Called by the semantics of the for-of statement.
	 */
	public static final SymbolValue iterator = new SymbolValue("Symbol.iterator");
	/**
	 * A regular expression method that matches the regular expression against a string.
	 * Called by the String.prototype.match method.
	 */
	public static final SymbolValue match = new SymbolValue("Symbol.match");
	/**
	 * A regular expression method that returns an iterator, that yields matches of the regular expression against a
	 * string. Called by the String.prototype.matchAll method.
	 */
	public static final SymbolValue matchAll = new SymbolValue("Symbol.matchAll");
	/**
	 * A regular expression method that replaces matched substrings of a string.
	 * Called by the String.prototype.replace method.
	 */
	public static final SymbolValue replace = new SymbolValue("Symbol.replace");
	/**
	 * A regular expression method that returns the index within a string that matches the regular expression.
	 * Called by the String.prototype.search method.
	 */
	public static final SymbolValue search = new SymbolValue("Symbol.search");
	/**
	 * A function valued property that is the constructor function that is used to create derived objects.
	 */
	public static final SymbolValue species = new SymbolValue("Symbol.species");
	/**
	 * A regular expression method that splits a string at the indices that match the regular expression.
	 * Called by the String.prototype.split method.
	 */
	public static final SymbolValue split = new SymbolValue("Symbol.split");
	/**
	 * A method that converts an object to a corresponding primitive value.
	 * Called by the ToPrimitive abstract operation.
	 */
	public static final SymbolValue toPrimitive = new SymbolValue("Symbol.toPrimitive");
	/**
	 * A String valued property that is used in the creation of the default string description of an object.
	 * Accessed by the built-in method Object.prototype.toString.
	 */
	public static final SymbolValue toStringTag = new SymbolValue("Symbol.toStringTag");
	/**
	 * An object valued property whose own and inherited property names are property names that are excluded
	 * from the with environment bindings of the associated object.
	 */
	public static final SymbolValue unscopables = new SymbolValue("Symbol.unscopables");
	public final String description;

	public SymbolValue(String description) {
		super(null, Value.Type.Symbol);
		this.description = description;
	}

	@Override
	public StringValue toStringValue(Interpreter interpreter) throws AbruptCompletion {
		throw AbruptCompletion.error(new TypeError("Cannot convert a Symbol value to a string"));
	}

	@Override
	public NumberValue toNumberValue(Interpreter interpreter) throws AbruptCompletion {
		throw AbruptCompletion.error(new TypeError("Cannot convert a Symbol value to a number"));
	}

	@Override
	public BooleanValue toBooleanValue(Interpreter interpreter) throws AbruptCompletion {
		return BooleanValue.TRUE;
	}

	@Override
	public ObjectValue toObjectValue(Interpreter interpreter) throws AbruptCompletion {
		throw new NotImplemented("SymbolWrapper");
	}

	@Override
	public String typeOf(Interpreter interpreter) throws AbruptCompletion {
		return "symbol";
	}

	@Override
	public void display(StringRepresentation builder) {
		builder.append("Symbol(");
		if (this.description != null) builder.append(this.description);
		builder.append(')');
	}
}
