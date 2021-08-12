package xyz.lebster.core.runtime.prototype;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.*;

public class FunctionPrototype extends Executable<Void> {
	private FunctionPrototype() {
		super(null);
		this.set("length", new NumericLiteral(0));
		this.set("name", new StringLiteral(""));
	}

	public static final FunctionPrototype instance = new FunctionPrototype();

	@Override
	protected Value<?> call(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		return Undefined.instance;
	}
}