package xyz.lebster.core.value;

import xyz.lebster.core.value.string.StringValue;

public final class Names {
	// Properties and methods
	public static final StringValue bind = new StringValue("bind");
	public static final StringValue call = new StringValue("call");
	public static final StringValue charAt = new StringValue("charAt");
	public static final StringValue charCodeAt = new StringValue("charCodeAt");
	public static final StringValue codePointAt = new StringValue("codePointAt");
	public static final StringValue concat = new StringValue("concat");
	public static final StringValue constructor = new StringValue("constructor");
	public static final StringValue create = new StringValue("create");
	public static final StringValue declare = new StringValue("declare");
	public static final StringValue description = new StringValue("description");
	public static final StringValue done = new StringValue("done");
	public static final StringValue endsWith = new StringValue("endsWith");
	public static final StringValue entries = new StringValue("entries");
	public static final StringValue equals = new StringValue("equals");
	public static final StringValue error = new StringValue("error");
	public static final StringValue eval = new StringValue("eval");
	public static final StringValue evaluate = new StringValue("evaluate");
	public static final StringValue expect = new StringValue("expect");
	public static final StringValue expectError = new StringValue("expectError");
	public static final StringValue fail = new StringValue("fail");
	public static final StringValue filter = new StringValue("filter");
	public static final StringValue forEach = new StringValue("forEach");
	public static final StringValue fromEntries = new StringValue("fromEntries");
	public static final StringValue getPrototypeOf = new StringValue("getPrototypeOf");
	public static final StringValue globalThis = new StringValue("globalThis");
	public static final StringValue group = new StringValue("group");
	public static final StringValue hasOwnProperty = new StringValue("hasOwnProperty");
	public static final StringValue hasProperty = new StringValue("hasProperty");
	public static final StringValue includes = new StringValue("includes");
	public static final StringValue indexOf = new StringValue("indexOf");
	public static final StringValue info = new StringValue("info");
	public static final StringValue input = new StringValue("input");
	public static final StringValue is = new StringValue("is");
	public static final StringValue isArray = new StringValue("isArray");
	public static final StringValue isFinite = new StringValue("isFinite");
	public static final StringValue isNaN = new StringValue("isNaN");
	public static final StringValue join = new StringValue("join");
	public static final StringValue keys = new StringValue("keys");
	public static final StringValue lastIndexOf = new StringValue("lastIndexOf");
	public static final StringValue length = new StringValue("length");
	public static final StringValue localeCompare = new StringValue("localeCompare");
	public static final StringValue log = new StringValue("log");
	public static final StringValue map = new StringValue("map");
	public static final StringValue message = new StringValue("message");
	public static final StringValue name = new StringValue("name");
	public static final StringValue next = new StringValue("next");
	public static final StringValue normalize = new StringValue("normalize");
	public static final StringValue of = new StringValue("of");
	public static final StringValue padEnd = new StringValue("padEnd");
	public static final StringValue padStart = new StringValue("padStart");
	public static final StringValue parseFloat = new StringValue("parseFloat");
	public static final StringValue parseInt = new StringValue("parseInt");
	public static final StringValue pop = new StringValue("pop");
	public static final StringValue prototype = new StringValue("prototype");
	public static final StringValue push = new StringValue("push");
	public static final StringValue random = new StringValue("random");
	public static final StringValue range = new StringValue("range");
	public static final StringValue reduce = new StringValue("reduce");
	public static final StringValue repeat = new StringValue("repeat");
	public static final StringValue replaceAll = new StringValue("replaceAll");
	public static final StringValue reverse = new StringValue("reverse");
	public static final StringValue setPrototypeOf = new StringValue("setPrototypeOf");
	public static final StringValue slice = new StringValue("slice");
	public static final StringValue startsWith = new StringValue("startsWith");
	public static final StringValue substring = new StringValue("substring");
	public static final StringValue toLocaleLowerCase = new StringValue("toLocaleLowerCase");
	public static final StringValue toLocaleString = new StringValue("toLocaleString");
	public static final StringValue toLocaleUpperCase = new StringValue("toLocaleUpperCase");
	public static final StringValue toLowerCase = new StringValue("toLowerCase");
	public static final StringValue toString = new StringValue("toString");
	public static final StringValue toUpperCase = new StringValue("toUpperCase");
	public static final StringValue trim = new StringValue("trim");
	public static final StringValue trimEnd = new StringValue("trimEnd");
	public static final StringValue trimStart = new StringValue("trimStart");
	public static final StringValue value = new StringValue("value");
	public static final StringValue valueOf = new StringValue("valueOf");
	public static final StringValue values = new StringValue("values");
	public static final StringValue warn = new StringValue("warn");
	public static final StringValue write = new StringValue("write");

	// Values
	public static final StringValue default_ = new StringValue("default");
	public static final StringValue string = new StringValue("string");
	public static final StringValue number = new StringValue("number");

	// Math constants
	public static final StringValue E = new StringValue("E");
	public static final StringValue LN2 = new StringValue("LN2");
	public static final StringValue LN10 = new StringValue("LN10");
	public static final StringValue LOG2E = new StringValue("LOG2E");
	public static final StringValue LOG10E = new StringValue("LOG10E");
	public static final StringValue PI = new StringValue("PI");
	public static final StringValue SQRT1_2 = new StringValue("SQRT1_2");
	public static final StringValue SQRT2 = new StringValue("SQRT2");

	// Globals
	public static final StringValue Infinity = new StringValue("Infinity");
	public static final StringValue NaN = new StringValue("NaN");
	public static final StringValue undefined = new StringValue("undefined");

	// Constructors & Global objects
	public static final StringValue Array = new StringValue("Array");
	public static final StringValue Boolean = new StringValue("Boolean");
	public static final StringValue Error = new StringValue("Error");
	public static final StringValue Function = new StringValue("Function");
	public static final StringValue Math = new StringValue("Math");
	public static final StringValue Number = new StringValue("Number");
	public static final StringValue Object = new StringValue("Object");
	public static final StringValue ShadowRealm = new StringValue("ShadowRealm");
	public static final StringValue String = new StringValue("String");
	public static final StringValue Symbol = new StringValue("Symbol");
	public static final StringValue Test = new StringValue("Test");
	public static final StringValue console = new StringValue("console");

	// Symbols
	public static final StringValue SymbolDotAsyncIterator = new StringValue("Symbol.asyncIterator");
	public static final StringValue SymbolDotHasInstance = new StringValue("Symbol.hasInstance");
	public static final StringValue SymbolDotIsConcatSpreadable = new StringValue("Symbol.isConcatSpreadable");
	public static final StringValue SymbolDotIterator = new StringValue("Symbol.iterator");
	public static final StringValue SymbolDotMatch = new StringValue("Symbol.match");
	public static final StringValue SymbolDotMatchAll = new StringValue("Symbol.matchAll");
	public static final StringValue SymbolDotReplace = new StringValue("Symbol.replace");
	public static final StringValue SymbolDotSearch = new StringValue("Symbol.search");
	public static final StringValue SymbolDotSpecies = new StringValue("Symbol.species");
	public static final StringValue SymbolDotSplit = new StringValue("Symbol.split");
	public static final StringValue SymbolDotToPrimitive = new StringValue("Symbol.toPrimitive");
	public static final StringValue SymbolDotToStringTag = new StringValue("Symbol.toStringTag");
	public static final StringValue SymbolDotUnscopables = new StringValue("Symbol.unscopables");
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

	public static final StringValue EMPTY = new StringValue("");
}
