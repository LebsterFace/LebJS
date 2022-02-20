package xyz.lebster.core.runtime.value.object.property;

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
}