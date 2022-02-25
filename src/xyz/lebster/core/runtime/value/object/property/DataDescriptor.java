package xyz.lebster.core.runtime.value.object.property;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.ObjectValue;

import java.util.HashSet;

public final class DataDescriptor implements PropertyDescriptor {
	private Value<?> value;
	private boolean writable;
	private boolean enumerable;
	private boolean configurable;

	public DataDescriptor(Value<?> value, boolean writable, boolean enumerable, boolean configurable) {
		this.value = value;
		this.writable = writable;
		this.enumerable = enumerable;
		this.configurable = configurable;
	}

	@Override
	public boolean isWritable() {
		return writable;
	}

	@Override
	public void setWritable(boolean writable) {
		this.writable = writable;
	}

	@Override
	public boolean isEnumerable() {
		return enumerable;
	}

	@Override
	public void setEnumerable(boolean enumerable) {
		this.enumerable = enumerable;
	}

	@Override
	public boolean isConfigurable() {
		return configurable;
	}

	@Override
	public void setConfigurable(boolean configurable) {
		this.configurable = configurable;
	}

	@Override
	public Value<?> get(Interpreter interpreter, ObjectValue thisValue) throws AbruptCompletion {
		return this.value;
	}

	@Override
	public void set(Interpreter interpreter, ObjectValue thisValue, Value<?> newValue) throws AbruptCompletion {
		this.value = newValue;
	}

	public void setRawValue(Value<?> newValue) {
		this.value = newValue;
	}

	public DataDescriptor copy() {
		return new DataDescriptor(value, writable, enumerable, configurable);
	}

	@Override
	public void display(StringRepresentation representation, ObjectValue parent, HashSet<ObjectValue> parents, boolean singleLine) {
		if (!(this.value instanceof final ObjectValue object)) {
			this.value.display(representation);
			return;
		}

		if (parents.contains(object)) {
			representation.append(ANSI.RED);

			if (object.getClass() != parent.getClass()) {
				object.representClassName(representation);
			} else {
				// TODO: <ref *1>
				representation.append(this.value == parent ? "[self]" : "[parent]");
			}

			representation.append(ANSI.RESET);
			return;
		}

		object.displayRecursive(representation, (HashSet<ObjectValue>) parents.clone(), singleLine);
	}
}