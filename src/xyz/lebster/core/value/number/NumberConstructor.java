package xyz.lebster.core.value.number;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.PrimitiveConstructor;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.TypeError;
import xyz.lebster.core.value.function.FunctionPrototype;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-number-constructor")
@NonStandard
public final class NumberConstructor extends PrimitiveConstructor {
	public NumberConstructor(FunctionPrototype functionPrototype) {
		super(functionPrototype, Names.Number);
		this.putMethod(functionPrototype, Names.range, NumberConstructor::range);
	}

	private static NumberRange range(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
		if (args.length == 0) throw AbruptCompletion.error(new TypeError(interpreter, "No end value was provided"));

		final double first = args[0].toNumberValue(interpreter).value;
		if (Double.isNaN(first))
			throw AbruptCompletion.error(new TypeError(interpreter, "NaN passed as first argument of Number.range"));
		if (args.length == 1) return new NumberRange(interpreter, first);

		final double second = args[1].toNumberValue(interpreter).value;
		if (Double.isNaN(second))
			throw AbruptCompletion.error(new TypeError(interpreter, "NaN passed as second argument of Number.range"));
		if (args.length == 2) return new NumberRange(interpreter, first, second);

		final double third = args[2].toNumberValue(interpreter).value;
		if (Double.isNaN(third))
			throw AbruptCompletion.error(new TypeError(interpreter, "NaN passed as third argument of Number.range"));
		return new NumberRange(interpreter, first, second, third);
	}

	@Override
	public NumberValue call(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		// 21.1.1.1 Number ( value )

		// 1. If value is present, then
		//     FIXME: a. Let prim be ? ToNumeric(value).
		//     FIXME: b. If Type(prim) is BigInt, let n be 𝔽(ℝ(prim)).
		//     c. Otherwise, let n be prim.
		// 2. Else,
		//     a. Let n be +0𝔽.

		// 3. If NewTarget is undefined, return n.
		return arguments.length > 0 ? arguments[0].toNumberValue(interpreter) : NumberValue.ZERO;
	}
}
