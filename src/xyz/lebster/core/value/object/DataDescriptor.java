package xyz.lebster.core.value.object;

import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;

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
	public void display(StringBuilder builder) {
		throw new ShouldNotHappen("DataDescriptor#display is handled by JSONDisplayer");
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