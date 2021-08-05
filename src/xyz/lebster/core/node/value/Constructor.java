package xyz.lebster.core.node.value;

public abstract class Constructor<V> extends Executable<V> {
	public Constructor(V code) {
		super(code);
	}

	public abstract Dictionary construct(Value<?>[] executedArguments);
}