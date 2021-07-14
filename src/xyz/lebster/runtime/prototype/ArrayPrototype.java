package xyz.lebster.runtime.prototype;

import xyz.lebster.exception.NotImplemented;
import xyz.lebster.node.value.*;
import xyz.lebster.runtime.ArrayObject;

public class ArrayPrototype extends Dictionary {
	public static final ArrayPrototype instance = new ArrayPrototype();

	private ArrayPrototype() {
		set("push", new NativeFunction((interpreter, arguments) -> {
			if (!(interpreter.thisValue() instanceof final ArrayObject arr)) {
				throw new NotImplemented("ArrayObject.length for non-ArrayObject Value");
			}

			return arr.push(arguments);
		}));
	}
}