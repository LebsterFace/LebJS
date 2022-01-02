package xyz.lebster.core.interpreter;

import xyz.lebster.cli.Testing;
import xyz.lebster.core.node.value.Dictionary;
import xyz.lebster.core.runtime.ConsoleObject;
import xyz.lebster.core.runtime.MathObject;

public final class GlobalObject extends Dictionary {
	public GlobalObject() {
		super();
		put("console", ConsoleObject.instance);
		put("Math", MathObject.instance);
		put("globalThis", this);
		Testing.addTestingMethods(this);
	}
}