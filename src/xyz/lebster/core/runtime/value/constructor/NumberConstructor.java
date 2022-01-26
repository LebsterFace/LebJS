package xyz.lebster.core.runtime.value.constructor;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.NumberWrapper;
import xyz.lebster.core.runtime.value.primitive.NumberValue;
import xyz.lebster.core.runtime.value.primitive.UndefinedValue;
import xyz.lebster.core.runtime.value.prototype.NumberPrototype;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string-constructor")
public class NumberConstructor extends BuiltinConstructor<NumberWrapper> {
	public static final NumberConstructor instance = new NumberConstructor();

	static {
		instance.putNonWritable(Names.prototype, NumberPrototype.instance);
	}

	private NumberConstructor() {
		super();
	}

	public NumberWrapper construct(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("new Number()");
	}

	@Override
	public NumberValue call(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		final Value<?> v = arguments.length == 0 ? UndefinedValue.instance : arguments[0];
		return v.toNumberValue(interpreter);
	}

	@Override
	protected String getName() {
		return "Number";
	}
}
