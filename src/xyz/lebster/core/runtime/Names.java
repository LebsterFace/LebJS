package xyz.lebster.core.runtime;

import xyz.lebster.core.runtime.value.primitive.StringValue;

public final class Names {
	// Properties and methods
	public static final StringValue bind = new StringValue("bind");
	public static final StringValue constructor = new StringValue("constructor");
	public static final StringValue done = new StringValue("done");
	public static final StringValue join = new StringValue("join");
	public static final StringValue length = new StringValue("length");
	public static final StringValue message = new StringValue("message");
	public static final StringValue name = new StringValue("name");
	public static final StringValue next = new StringValue("next");
	public static final StringValue prototype = new StringValue("prototype");
	public static final StringValue range = new StringValue("range");
	public static final StringValue toString = new StringValue("toString");
	public static final StringValue value = new StringValue("value");
	public static final StringValue valueOf = new StringValue("valueOf");
	public static final StringValue values = new StringValue("values");

	// Globals
	public static final StringValue Infinity = new StringValue("Infinity");
	public static final StringValue NaN = new StringValue("NaN");
	public static final StringValue undefined = new StringValue("undefined");

	// Constructors & Global objects
	public static final StringValue Array = new StringValue("Array");
	public static final StringValue Boolean = new StringValue("Boolean");
	public static final StringValue Math = new StringValue("Math");
	public static final StringValue Number = new StringValue("Number");
	public static final StringValue Object = new StringValue("Object");
	public static final StringValue ShadowRealm = new StringValue("ShadowRealm");
	public static final StringValue String = new StringValue("String");
	public static final StringValue Symbol = new StringValue("Symbol");
	public static final StringValue console = new StringValue("console");

	// Symbols
	public static final StringValue asyncIterator = new StringValue("asyncIterator");
	public static final StringValue hasInstance = new StringValue("hasInstance");
	public static final StringValue isConcatSpreadable = new StringValue("isConcatSpreadable");
	public static final StringValue iterator = new StringValue("iterator");
	public static final StringValue match = new StringValue("match");
	public static final StringValue matchAll = new StringValue("matchAll");
	public static final StringValue replace = new StringValue("replace");
	public static final StringValue search = new StringValue("search");
	public static final StringValue species = new StringValue("species");
	public static final StringValue split = new StringValue("split");
	public static final StringValue toPrimitive = new StringValue("toPrimitive");
	public static final StringValue toStringTag = new StringValue("toStringTag");
	public static final StringValue unscopables = new StringValue("unscopables");
}
