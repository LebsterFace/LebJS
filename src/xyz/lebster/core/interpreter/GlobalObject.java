package xyz.lebster.core.interpreter;

import xyz.lebster.cli.Testing;
import xyz.lebster.core.node.value.Dictionary;
import xyz.lebster.core.node.value.NumericLiteral;
import xyz.lebster.core.node.value.Undefined;
import xyz.lebster.core.runtime.ConsoleObject;
import xyz.lebster.core.runtime.MathObject;

public final class GlobalObject extends Dictionary {
	public GlobalObject() {
		super();
		put("console", ConsoleObject.instance);
		put("Math", MathObject.instance);
		put("globalThis", this);

		put("NaN", new NumericLiteral(Double.NaN));
		put("Infinity", new NumericLiteral(Double.POSITIVE_INFINITY));
		put("undefined", Undefined.instance);

		Testing.addTestingMethods(this);
	}
}