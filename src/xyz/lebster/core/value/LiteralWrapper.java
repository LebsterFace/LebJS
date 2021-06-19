package xyz.lebster.core.value;

abstract public class LiteralWrapper<T> extends Dictionary {
	public final T value;
	public LiteralWrapper(T value) {
		this.value = value;
	}
}
