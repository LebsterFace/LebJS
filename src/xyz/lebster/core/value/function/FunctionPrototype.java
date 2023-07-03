package xyz.lebster.core.value.function;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.string.StringValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

import static xyz.lebster.core.value.array.ArrayPrototype.createListFromArrayLike;
import static xyz.lebster.core.value.function.NativeFunction.argument;
import static xyz.lebster.core.value.function.NativeFunction.argumentRest;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-function.prototype")
@NonStandard
public final class FunctionPrototype extends ObjectValue {
	public FunctionPrototype(Intrinsics intrinsics) {
		super(intrinsics);
		// NOTE: `this` is used instead of `intrinsics` because Function.prototype has not been initialised yet
		putMethod(this, Names.apply, 2, FunctionPrototype::apply);
		putMethod(this, Names.bind, 1, FunctionPrototype::bind);
		putMethod(this, Names.call, 1, FunctionPrototype::call);
		putMethod(this, Names.toString, 0, FunctionPrototype::toStringMethod);
		putMethod(this, SymbolValue.hasInstance, 1, FunctionPrototype::hasInstance, false, false, false);
	}


	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-function.prototype.apply")
	private static Value<?> apply(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.2.3.1 Function.prototype.apply ( thisArg, argArray )
		final Value<?> thisArg = argument(0, arguments);
		final Value<?> argArray = argument(1, arguments);

		// 1. Let func be the `this` value.
		final Value<?> func_ = interpreter.thisValue();
		// 2. If IsCallable(func) is false, throw a TypeError exception.
		final Executable func = Executable.getExecutable(interpreter, func_);
		// 3. If argArray is either undefined or null, then
		if (argArray.isNullish()) {
			// TODO: a. Perform PrepareForTailCall().
			// b. Return ? Call(func, thisArg).
			return func.call(interpreter, thisArg);
		}

		// 4. Let argList be ? CreateListFromArrayLike(argArray).
		final Value<?>[] argList = createListFromArrayLike(interpreter, argArray);
		// TODO: 5. Perform PrepareForTailCall().
		// 6. Return ? Call(func, thisArg, argList).
		return func.call(interpreter, thisArg, argList);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-function.prototype.bind")
	private static Value<?> bind(Interpreter interpreter, Value<?>[] arguments) {
		// 20.2.3.2 Function.prototype.bind ( thisArg, ...args )
		final Value<?> thisArg = argument(0, arguments);
		final Value<?>[] args = argumentRest(1, arguments);

		throw new NotImplemented("Function.prototype.bind()");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-function.prototype.call")
	private static Value<?> call(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.2.3.3 Function.prototype.call ( thisArg, ...args )
		final Value<?> thisArg = argument(0, arguments);
		final Value<?>[] args = argumentRest(1, arguments);

		// 1. Let func be the `this` value.
		final Value<?> func_ = interpreter.thisValue();
		// 2. If IsCallable(func) is false, throw a TypeError exception.
		final Executable func = Executable.getExecutable(interpreter, func_);
		// TODO: 3. Perform PrepareForTailCall().
		// 4. Return ? Call(func, thisArg, args).
		return func.call(interpreter, thisArg, args);
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-function.prototype.tostring")
	private static StringValue toStringMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.2.3.5 Function.prototype.toString ( )

		return Executable.getExecutable(interpreter, interpreter.thisValue()).toStringMethod();
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-function.prototype-@@hasinstance")
	private static BooleanValue hasInstance(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.2.3.6 Function.prototype [ @@hasInstance ] ( V )
		final Value<?> V = argument(0, arguments);

		// 1. Let F be the `this` value.
		final Value<?> F = interpreter.thisValue();
		// 2. Return ? OrdinaryHasInstance(F, V).
		return Executable.ordinaryHasInstance(interpreter, F, V);
	}
}