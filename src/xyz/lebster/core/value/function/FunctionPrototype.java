package xyz.lebster.core.value.function;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.util.Arrays;

import static xyz.lebster.core.value.function.NativeFunction.argument;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-function.prototype")
@NonStandard
public final class FunctionPrototype extends ObjectValue {
	public FunctionPrototype(Intrinsics intrinsics) {
		super(intrinsics);
		// TODO: .bind, .apply, .constructor, [ @@hasInstance ]
		putMethod(this, Names.call, 1, FunctionPrototype::callMethod);
		putMethod(this, Names.toString, 0, FunctionPrototype::toStringMethod);
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-function.prototype.tostring")
	private static StringValue toStringMethod(Interpreter interpreter, Value<?>[] values) throws AbruptCompletion {
		// 20.2.3.5 Function.prototype.toString ( )

		return Executable.getExecutable(interpreter, interpreter.thisValue()).toStringMethod();
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-function.prototype.call")
	@NonCompliant
	private static Value<?> callMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.2.3.3 Function.prototype.call ( thisArg, ...args )
		final Value<?> thisArg = argument(0, arguments);

		final Executable func = Executable.getExecutable(interpreter, interpreter.thisValue());
		final Value<?>[] args = Arrays.copyOfRange(arguments, 1, arguments.length);
		return func.call(interpreter, thisArg, args);
	}
}