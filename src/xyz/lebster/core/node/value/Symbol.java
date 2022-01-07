package xyz.lebster.core.node.value;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.object.ObjectValue;
import xyz.lebster.core.runtime.TypeError;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-ecmascript-language-types-symbol-type")
public class Symbol extends ObjectValue.Key<Void> {
	// https://tc39.es/ecma262/multipage#table-well-known-symbols

	/**
	 * A method that returns the default AsyncIterator for an object.
	 * Called by the semantics of the for-await-of statement.
	 */
	public static final Symbol asyncIterator = new Symbol("Symbol.asyncIterator");
	/**
	 * A method that determines if a constructor object recognizes an object as one of the constructor's instances.
	 * Called by the semantics of the instanceof operator.
	 */
	public static final Symbol hasInstance = new Symbol("Symbol.hasInstance");
	/**
	 * A Boolean valued property that if true indicates that an object should be flattened to
	 * its array elements by Array.prototype.concat.
	 */
	public static final Symbol isConcatSpreadable = new Symbol("Symbol.isConcatSpreadable");
	/**
	 * A method that returns the default Iterator for an object. Called by the semantics of the for-of statement.
	 */
	public static final Symbol iterator = new Symbol("Symbol.iterator");
	/**
	 * A regular expression method that matches the regular expression against a string.
	 * Called by the String.prototype.match method.
	 */
	public static final Symbol match = new Symbol("Symbol.match");
	/**
	 * A regular expression method that returns an iterator, that yields matches of the regular expression against a
	 * string. Called by the String.prototype.matchAll method.
	 */
	public static final Symbol matchAll = new Symbol("Symbol.matchAll");
	/**
	 * A regular expression method that replaces matched substrings of a string.
	 * Called by the String.prototype.replace method.
	 */
	public static final Symbol replace = new Symbol("Symbol.replace");
	/**
	 * A regular expression method that returns the index within a string that matches the regular expression.
	 * Called by the String.prototype.search method.
	 */
	public static final Symbol search = new Symbol("Symbol.search");
	/**
	 * A function valued property that is the constructor function that is used to create derived objects.
	 */
	public static final Symbol species = new Symbol("Symbol.species");
	/**
	 * A regular expression method that splits a string at the indices that match the regular expression.
	 * Called by the String.prototype.split method.
	 */
	public static final Symbol split = new Symbol("Symbol.split");
	/**
	 * A method that converts an object to a corresponding primitive value.
	 * Called by the ToPrimitive abstract operation.
	 */
	public static final Symbol toPrimitive = new Symbol("Symbol.toPrimitive");
	/**
	 * A String valued property that is used in the creation of the default string description of an object.
	 * Accessed by the built-in method Object.prototype.toString.
	 */
	public static final Symbol toStringTag = new Symbol("Symbol.toStringTag");
	/**
	 * An object valued property whose own and inherited property names are property names that are excluded
	 * from the with environment bindings of the associated object.
	 */
	public static final Symbol unscopables = new Symbol("Symbol.unscopables");
	public final String description;

	public Symbol(String description) {
		super(null, Type.Symbol);
		this.description = description;
	}

	@Override
	public StringValue toStringLiteral(Interpreter interpreter) throws AbruptCompletion {
		throw AbruptCompletion.error(new TypeError("Cannot convert a Symbol value to a string"));
	}

	@Override
	public NumberValue toNumericLiteral(Interpreter interpreter) throws AbruptCompletion {
		throw AbruptCompletion.error(new TypeError("Cannot convert a Symbol value to a number"));
	}

	@Override
	public BooleanValue toBooleanLiteral(Interpreter interpreter) throws AbruptCompletion {
		return BooleanValue.TRUE;
	}

	@Override
	public ObjectValue toObjectLiteral(Interpreter interpreter) throws AbruptCompletion {
		throw new NotImplemented("SymbolWrapper");
	}

	@Override
	public String typeOf(Interpreter interpreter) throws AbruptCompletion {
		return "symbol";
	}
}
