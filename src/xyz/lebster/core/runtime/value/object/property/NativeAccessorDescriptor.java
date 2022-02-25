package xyz.lebster.core.runtime.value.object.property;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.object.ObjectValue;

import java.util.HashSet;

public abstract class NativeAccessorDescriptor implements PropertyDescriptor {
	private boolean configurable;

	public NativeAccessorDescriptor(boolean configurable) {
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
		return true;
	}

	@Override
	public void setEnumerable(boolean enumerable) {
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
	public final void display(StringRepresentation representation, ObjectValue parent, HashSet<ObjectValue> parents, boolean singleLine) {
		representation.append(ANSI.MAGENTA);
		representation.append("[Getter/Setter]");
		representation.append(ANSI.RESET);
	}
}