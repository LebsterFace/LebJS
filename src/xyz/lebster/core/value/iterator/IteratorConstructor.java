package xyz.lebster.core.value.iterator;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.BuiltinConstructor;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;

@SpecificationURL("https://tc39.es/proposal-iterator-helpers#sec-iterator-constructor")
public final class IteratorConstructor extends BuiltinConstructor<IteratorObject, IteratorPrototype> {
	public IteratorConstructor(Intrinsics intrinsics) {
		super(intrinsics, Names.Iterator, 0);
	}

	@Override
	@SpecificationURL("https://tc39.es/proposal-iterator-helpers#sec-iterator")
	public IteratorObject construct(Interpreter interpreter, Value<?>[] arguments, ObjectValue newTarget) throws AbruptCompletion {
		throw new NotImplemented("new Iterator()");
	}
}
