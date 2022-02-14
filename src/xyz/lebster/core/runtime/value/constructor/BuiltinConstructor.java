package xyz.lebster.core.runtime.value.constructor;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.executable.Constructor;
import xyz.lebster.core.runtime.value.object.ObjectValue;

import java.util.HashSet;

abstract class BuiltinConstructor<T extends ObjectValue> extends Constructor<Void> {
	BuiltinConstructor() {
		super(null);
	}

	@Override
	public abstract T construct(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion;

	@Override
	public void display(StringRepresentation representation) {
		objectValue__display(representation);
	}

	@Override
	public void displayRecursive(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
		objectValue__displayRecursive(representation, parents, singleLine);
	}
}
