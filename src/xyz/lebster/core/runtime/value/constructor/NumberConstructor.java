package xyz.lebster.core.runtime.value.constructor;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.object.NumberRange;
import xyz.lebster.core.runtime.value.object.NumberWrapper;
import xyz.lebster.core.runtime.value.primitive.NumberValue;
import xyz.lebster.core.runtime.value.primitive.Undefined;
import xyz.lebster.core.runtime.value.prototype.NumberPrototype;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string-constructor")
public final class NumberConstructor extends BuiltinConstructor<NumberWrapper> {
	public static final NumberConstructor instance = new NumberConstructor();

	static {
		instance.putNonWritable(Names.prototype, NumberPrototype.instance);
		instance.putMethod(Names.range, NumberConstructor::range);
	}

	private NumberConstructor() {
		super(Names.Number);
	}

	private static Value<?> range(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
		if (args.length == 0) throw AbruptCompletion.error(new TypeError("No end value was provided"));

		final double first = args[0].toNumberValue(interpreter).value;
		if (Double.isNaN(first))
			throw AbruptCompletion.error(new TypeError("NaN passed as first argument of Number.range"));
		if (args.length == 1) return new NumberRange(first);

		final double second = args[1].toNumberValue(interpreter).value;
		if (Double.isNaN(second))
			throw AbruptCompletion.error(new TypeError("NaN passed as second argument of Number.range"));
		if (args.length == 2) return new NumberRange(first, second);

		final double third = args[2].toNumberValue(interpreter).value;
		if (Double.isNaN(third))
			throw AbruptCompletion.error(new TypeError("NaN passed as third argument of Number.range"));
		return new NumberRange(first, second, third);
	}

	public NumberWrapper construct(Interpreter interpreter, Value<?>[] arguments) {
		throw new NotImplemented("new Number()");
	}

	@Override
	public NumberValue call(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		final Value<?> v = arguments.length == 0 ? Undefined.instance : arguments[0];
		return v.toNumberValue(interpreter);
	}
}
