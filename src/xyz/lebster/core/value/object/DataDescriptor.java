package xyz.lebster.core.value.object;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;

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

	public static void display(Value<?> value, StringRepresentation representation, ObjectValue parent, HashSet<ObjectValue> parents, boolean singleLine) {
		if (!(value instanceof final ObjectValue object)) {
			value.display(representation);
			return;
		}

		if (parents.contains(object)) {
			representation.append(ANSI.RED);

			if (
				object.getClass() == ObjectValue.class ||
				object.getClass() == ArrayObject.class ||
				object.getClass() == parent.getClass()
			) {
				// TODO: Shortest-path property collapsing
				//       e.g. obj = { self: <obj>, child: {}, array: [<obj.child>] }
				//       or '<ref *1>' & '[Circular *1]'
				representation.append(value == parent ? "[self]" : "[parent]");
			} else {
				ObjectValue.representClassName(representation, object.getClass().getSimpleName());
			}

			representation.append(ANSI.RESET);
			return;
		}

		// noinspection unchecked
		object.displayRecursive(representation, (HashSet<ObjectValue>) parents.clone(), singleLine);
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
	public Value<?> get(Interpreter interpreter, ObjectValue thisValue) {
		return this.value;
	}

	public Value<?> value() {
		return this.value;
	}

	@Override
	public void set(Interpreter interpreter, ObjectValue thisValue, Value<?> newValue) {
		this.value = newValue;
	}

	public DataDescriptor copy() {
		return new DataDescriptor(value, writable, enumerable, configurable);
	}

	@Override
	public void display(StringRepresentation representation, ObjectValue parent, HashSet<ObjectValue> parents, boolean singleLine) {
		parents.add(parent);
		DataDescriptor.display(this.value, representation, parent, parents, singleLine);
	}

	@Override
	public ObjectValue fromPropertyDescriptor(Interpreter interpreter) throws AbruptCompletion {
		final var obj = new ObjectValue(interpreter.intrinsics);
		// TODO: Make CreateDataPropertyOrThrow
		obj.set(interpreter, Names.value, value);
		obj.set(interpreter, Names.writable, BooleanValue.of(isWritable()));
		obj.set(interpreter, Names.enumerable, BooleanValue.of(isEnumerable()));
		obj.set(interpreter, Names.configurable, BooleanValue.of(isConfigurable()));
		return obj;
	}
}