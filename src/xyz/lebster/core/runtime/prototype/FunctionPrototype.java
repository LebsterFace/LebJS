package xyz.lebster.core.runtime.prototype;

import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.*;

public class FunctionPrototype extends Executable<Void> {
	public static final FunctionPrototype instance = new FunctionPrototype();

	static {
		instance.put("length", new NumericLiteral(0));
		instance.put("name", new StringLiteral(""));
	}

	private FunctionPrototype() {
		super(null);
	}

	@Override
	protected Value<?> internalCall(Interpreter interpreter, Value<?>... arguments) {
		return Undefined.instance;
	}
}