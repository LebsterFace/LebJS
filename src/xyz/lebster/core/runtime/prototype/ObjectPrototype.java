package xyz.lebster.core.runtime.prototype;

import xyz.lebster.core.node.value.Dictionary;
import xyz.lebster.core.node.value.NativeFunction;
import xyz.lebster.core.node.value.StringLiteral;

public final class ObjectPrototype extends Dictionary {
	public static final ObjectPrototype instance = new ObjectPrototype();
	private ObjectPrototype() {
//		https://tc39.es/ecma262/multipage#sec-object.prototype.tostring
		set("toString", new NativeFunction((interpreter, arguments) -> {
//			FIXME: Follow spec
			return new StringLiteral("[object Object]");
		}));

//		https://tc39.es/ecma262/multipage#sec-object.prototype.valueof
		set("valueOf", new NativeFunction((interpreter, arguments) -> {
//			1. Return ? ToObject(this value).
			return interpreter.thisValue().toDictionary(interpreter);
		}));
	}

	@Override
	public Dictionary getPrototype() {
		return null;
	}
}