package xyz.lebster.core.runtime.value.constructor;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.executable.Function;
import xyz.lebster.core.runtime.value.prototype.FunctionPrototype;
import xyz.lebster.core.runtime.value.prototype.ObjectPrototype;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string-constructor")
public class FunctionConstructor extends BuiltinConstructor<Function, FunctionPrototype> {
	public FunctionConstructor(ObjectPrototype objectPrototype, FunctionPrototype functionPrototype) {
		super(objectPrototype, functionPrototype, Names.Function);
	}

	public Function construct(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("new Function()");
	}

	@Override
	public Function call(Interpreter interpreter, Value<?>... arguments) {
		throw new NotImplemented("Function()");
	}
}
