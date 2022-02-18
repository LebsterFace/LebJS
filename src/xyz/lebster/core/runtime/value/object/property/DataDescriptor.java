package xyz.lebster.core.runtime.value.object.property;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.ObjectValue;

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

	public Value<?> getRawValue() {
		return this.value;
	}

	public void setRawValue(Value<?> newValue) {
		this.value = newValue;
	}

	public DataDescriptor copy() {
		return new DataDescriptor(value, writable, enumerable, configurable);
	}
}