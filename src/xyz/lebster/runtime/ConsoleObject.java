package xyz.lebster.runtime;

import xyz.lebster.node.value.*;

public final class ConsoleObject extends Dictionary {
	public static final ConsoleObject instance = new ConsoleObject();
	private ConsoleObject() {
		set("log", new NativeFunction(((interpreter, arguments) -> {
			for (Value<?> value : arguments) {
				final StringLiteral str = value.toStringLiteral();
				System.out.println(str.value);
			}

			return new Undefined();
		})));
	}
}