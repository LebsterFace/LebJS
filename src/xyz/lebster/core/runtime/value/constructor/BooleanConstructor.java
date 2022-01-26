package xyz.lebster.core.runtime.value.constructor;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.BooleanWrapper;
import xyz.lebster.core.runtime.value.primitive.BooleanValue;
import xyz.lebster.core.runtime.value.primitive.UndefinedValue;
import xyz.lebster.core.runtime.value.prototype.NumberPrototype;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string-constructor")
public class BooleanConstructor extends BuiltinConstructor<BooleanWrapper> {
	public static final BooleanConstructor instance = new BooleanConstructor();

	static {
		instance.putNonWritable(Names.prototype, NumberPrototype.instance);
	}

	private BooleanConstructor() {
		super();
	}

	public BooleanWrapper construct(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("new Boolean()");
	}

	@Override
	public BooleanValue call(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		final Value<?> v = arguments.length == 0 ? UndefinedValue.instance : arguments[0];
		return v.toBooleanValue(interpreter);
	}

	@Override
	protected String getName() {
		return "Boolean";
	}
}
