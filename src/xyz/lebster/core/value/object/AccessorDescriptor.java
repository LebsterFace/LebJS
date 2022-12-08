package xyz.lebster.core.value.object;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;

import java.util.HashSet;

public final class AccessorDescriptor implements PropertyDescriptor {
	private final Executable getter;
	private final Executable setter;
	private boolean enumerable;
	private boolean configurable;

	public AccessorDescriptor(Executable getter, Executable setter, boolean enumerable, boolean configurable) {
		this.getter = getter;
		this.setter = setter;
		this.enumerable = enumerable;
		this.configurable = configurable;
	}

	@Override
	public boolean isWritable() {
		return true;
	}

	@Override
	public void setWritable(boolean writable) {
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
		return this.getter.call(interpreter, thisValue);
	}

	@Override
	public void set(Interpreter interpreter, ObjectValue thisValue, Value<?> newValue) throws AbruptCompletion {
		this.setter.call(interpreter, thisValue, newValue);
	}

	@Override
	public void display(StringRepresentation representation, ObjectValue parent, HashSet<ObjectValue> parents, boolean singleLine) {
		representation.append(ANSI.CYAN);
		representation.append("[Getter/Setter]");
		representation.append(ANSI.RESET);
	}

	@Override
	public ObjectValue fromPropertyDescriptor(Interpreter interpreter) throws AbruptCompletion {
		final var obj = new ObjectValue(interpreter.intrinsics);
		// TODO: Make CreateDataPropertyOrThrow
		obj.set(interpreter, Names.get, getter);
		obj.set(interpreter, Names.set, setter);
		obj.set(interpreter, Names.writable, BooleanValue.of(isWritable()));
		obj.set(interpreter, Names.enumerable, BooleanValue.of(isEnumerable()));
		obj.set(interpreter, Names.configurable, BooleanValue.of(isConfigurable()));
		return obj;
	}
}