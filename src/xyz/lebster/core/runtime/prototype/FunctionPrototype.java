package xyz.lebster.core.runtime.prototype;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.*;

public class FunctionPrototype extends Executable<Void> {
	public static final FunctionPrototype instance = new FunctionPrototype();

	private FunctionPrototype() {
		super(null);
		this.set("length", new NumericLiteral(0));
		this.set("name", new StringLiteral(""));
	}

	@Override
	protected Value<?> call(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		return Undefined.instance;
	}
}