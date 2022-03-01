package xyz.lebster.core.runtime.value.constructor;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.executable.Function;
import xyz.lebster.core.runtime.value.prototype.FunctionPrototype;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string-constructor")
public class FunctionConstructor extends BuiltinConstructor<Function> {
	public static final FunctionConstructor instance = new FunctionConstructor();

	static {
		instance.putNonWritable(Names.prototype, FunctionPrototype.instance);
	}

	private FunctionConstructor() {
		super(Names.Function);
	}

	public Function construct(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("new Function()");
	}

	@Override
	public Function call(Interpreter interpreter, Value<?>... arguments) {
		throw new NotImplemented("Function()");
	}
}
