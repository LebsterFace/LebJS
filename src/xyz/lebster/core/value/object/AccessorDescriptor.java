package xyz.lebster.core.value.object;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Displayable;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;

public final class AccessorDescriptor implements PropertyDescriptor, Displayable {
	private Executable getter;
	private Executable setter;
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
		if (getter == null) return Undefined.instance;
		return getter.call(interpreter, thisValue);
	}

	@Override
	public void set(Interpreter interpreter, ObjectValue thisValue, Value<?> newValue) throws AbruptCompletion {
		if (setter != null) {
			setter.call(interpreter, thisValue, newValue);
		}
	}

	public void setGetter(Executable getter) {
		this.getter = getter;
	}

	public void setSetter(Executable setter) {
		this.setter = setter;
	}

	@Override
	public ObjectValue fromPropertyDescriptor(Interpreter interpreter) throws AbruptCompletion {
		final var obj = new ObjectValue(interpreter.intrinsics);
		// TODO: Make CreateDataPropertyOrThrow
		obj.set(interpreter, Names.get, getter == null ? Undefined.instance : getter);
		obj.set(interpreter, Names.set, setter == null ? Undefined.instance : setter);
		obj.set(interpreter, Names.writable, BooleanValue.of(isWritable()));
		obj.set(interpreter, Names.enumerable, BooleanValue.of(isEnumerable()));
		obj.set(interpreter, Names.configurable, BooleanValue.of(isConfigurable()));
		return obj;
	}

	@Override
	public void display(StringRepresentation representation) {
		representation.append(ANSI.CYAN);
		representation.append("[Getter");
		if (setter != null) representation.append("/Setter");
		representation.append(']');
		representation.append(ANSI.RESET);
	}
}