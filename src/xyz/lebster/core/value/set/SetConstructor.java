package xyz.lebster.core.value.set;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.BuiltinConstructor;
import xyz.lebster.core.value.IteratorHelper;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.object.ObjectValue;

import java.util.ArrayList;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.function.NativeFunction.argument;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set-constructor")
public class SetConstructor extends BuiltinConstructor<SetObject, SetPrototype> {
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-set-constructor")
	public SetConstructor(Intrinsics intrinsics) {
		super(intrinsics, Names.Set);
		// TODO: 24.2.2.2 get Set [ @@species ]
	}

	@Override
	public SetObject internalCall(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		throw error(new TypeError(interpreter, "Set constructor must be called with `new`"));
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-set-iterable")
	public SetObject construct(Interpreter interpreter, Value<?>[] arguments, ObjectValue newTarget) throws AbruptCompletion {
		// 24.2.1.1 Set ( [ iterable ] )
		final Value<?> iterable = argument(0, arguments);

		// 3. Set set.[[SetData]] to a new empty List.
		final var setData = new ArrayList<Value<?>>();

		// FIXME: 2. Let set be ? OrdinaryCreateFromConstructor(NewTarget, "%Set.prototype%", « [[SetData]] »).
		final var set = new SetObject(interpreter.intrinsics, setData);

		// 4. If iterable is either undefined or null, return set.
		if (iterable.isNullish()) return set;
		// 5. Let adder be ? Get(set, "add").
		final var potential_adder = set.get(interpreter, Names.add);
		// 6. If IsCallable(adder) is false, throw a TypeError exception.
		final Executable adder = Executable.getExecutable(interpreter, potential_adder);
		// 7. Let iteratorRecord be ? GetIterator(iterable).
		final var iteratorRecord = IteratorHelper.getIterator(interpreter, iterable);
		// 8. Repeat,
		while (true) {
			// a. Let next be ? IteratorStep(iteratorRecord).
			final var next = iteratorRecord.step(interpreter);
			// b. If next is false, return set.
			if (next == null) return set;
			// c. Let nextValue be ? IteratorValue(next).
			final Value<?> nextValue = IteratorHelper.iteratorValue(interpreter, next);
			// d. Let status be Completion(Call(adder, set, « nextValue »)).
			final Value<?> status = adder.call(interpreter, set, nextValue);
			// FIXME: e. IfAbruptCloseIterator(status, iteratorRecord).
		}
	}
}
