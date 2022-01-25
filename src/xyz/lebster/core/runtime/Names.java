package xyz.lebster.core.runtime;

import xyz.lebster.core.runtime.value.primitive.StringValue;

public final class Names {
	public static final StringValue join = new StringValue("join");
	public static final StringValue toString = new StringValue("toString");
	public static final StringValue valueOf = new StringValue("valueOf");
	public static final StringValue bind = new StringValue("bind");

	public static final StringValue NaN = new StringValue("NaN");
	public static final StringValue undefined = new StringValue("undefined");
	public static final StringValue Infinity = new StringValue("Infinity");

	public static final StringValue Math = new StringValue("Math");
	public static final StringValue Object = new StringValue("Object");
	public static final StringValue Array = new StringValue("Array");
	public static final StringValue String = new StringValue("String");
	public static final StringValue Number = new StringValue("Number");
	public static final StringValue console = new StringValue("console");

	public static final StringValue length = new StringValue("length");
	public static final StringValue prototype = new StringValue("prototype");
	public static final StringValue name = new StringValue("name");
	public static final StringValue message = new StringValue("message");
	public static final StringValue constructor = new StringValue("constructor");
}
