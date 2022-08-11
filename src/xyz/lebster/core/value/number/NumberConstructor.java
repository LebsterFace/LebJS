package xyz.lebster.core.value.number;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.BuiltinConstructor;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.TypeError;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectPrototype;
import xyz.lebster.core.value.object.ObjectValue;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-number-constructor")
public final class NumberConstructor extends BuiltinConstructor<NumberWrapper, NumberPrototype> {
	public NumberConstructor(ObjectPrototype objectPrototype, FunctionPrototype fp) {
		super(objectPrototype, fp, Names.Number);
		this.putMethod(fp, Names.range, NumberConstructor::range);
	}

	private static Value<?> range(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
		if (args.length == 0) throw AbruptCompletion.error(new TypeError(interpreter, "No end value was provided"));

		final double first = args[0].toNumberValue(interpreter).value;
		if (Double.isNaN(first))
			throw AbruptCompletion.error(new TypeError(interpreter, "NaN passed as first argument of Number.range"));
		if (args.length == 1) return new NumberRange(interpreter.intrinsics.functionPrototype, first);

		final double second = args[1].toNumberValue(interpreter).value;
		if (Double.isNaN(second))
			throw AbruptCompletion.error(new TypeError(interpreter, "NaN passed as second argument of Number.range"));
		if (args.length == 2) return new NumberRange(interpreter.intrinsics.functionPrototype, first, second);

		final double third = args[2].toNumberValue(interpreter).value;
		if (Double.isNaN(third))
			throw AbruptCompletion.error(new TypeError(interpreter, "NaN passed as third argument of Number.range"));
		return new NumberRange(interpreter.intrinsics.functionPrototype, first, second, third);
	}

	public NumberWrapper construct(Interpreter interpreter, Value<?>[] arguments, ObjectValue newTarget) throws AbruptCompletion {
		final NumberValue n = this.call(interpreter, arguments);
		// 4. Let O be ? OrdinaryCreateFromConstructor(NewTarget, "%Number.prototype%", « [[NumberData]] »).
		// 5. Set O.[[NumberData]] to n.
		// 6. Return O.
		return new NumberWrapper(interpreter.intrinsics.numberPrototype, n);
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
