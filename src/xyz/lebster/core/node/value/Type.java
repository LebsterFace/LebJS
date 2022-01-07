package xyz.lebster.core.node.value;

import xyz.lebster.core.SpecificationURL;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-primitive-value")
public enum Type {
	String,
	Symbol,
	// TODO: BigInt
	Number,
	Boolean,
	Object,
	Null,
	Undefined
}