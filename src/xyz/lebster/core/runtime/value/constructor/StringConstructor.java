package xyz.lebster.core.runtime.value.constructor;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.StringWrapper;
import xyz.lebster.core.runtime.value.prototype.StringPrototype;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string-constructor")
public class StringConstructor extends BuiltinConstructor<StringWrapper> {
	public static final StringConstructor instance = new StringConstructor();

	static {
		instance.putNonWritable(Names.prototype, StringPrototype.instance);
	}

	private StringConstructor() {
		super();
	}

	public StringWrapper construct(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("new String()");
	}

	@Override
	protected Value<?> internalCall(Interpreter interpreter, Value<?>... arguments) {
		throw new NotImplemented("String()");
	}

	@Override
	protected String getName() {
		return "String";
	}
}
