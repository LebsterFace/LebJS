package xyz.lebster.core.runtime.value.object.property;

public abstract class NativeAccessorDescriptor implements PropertyDescriptor {
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
		return true;
	}

	@Override
	public void setConfigurable(boolean configurable) {
	}
}