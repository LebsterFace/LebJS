package xyz.lebster.core.value;

import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.exception.NotImplemented;

import java.util.HashMap;
import java.util.Map;

public class Dictionary extends Value<HashMap<StringLiteral, Value<?>>> {
	public Dictionary() {
		super(Type.Dictionary, new HashMap<>());
	}

	public Value<?> set(StringLiteral name, Value<?> value) {
		return this.value.put(name, value);
	}

	@SuppressWarnings("UnusedReturnValue")
	public Value<?> set(String name, Value<?> value) {
		return set(new StringLiteral(name), value);
	}

	public Value<?> get(StringLiteral name) {
		Dictionary object = this;

		while (true) {
			if (object == null) {
				// End of prototype chain; property does not exist.
				return new Undefined();
			} else if (object.value.containsKey(name)) {
				// Property was found
				return object.value.get(name);
			} else {
				// Property does not exist on current object. Move up prototype chain
				object = object.getPrototype();
			}
		}
	}

	@Override
	public StringLiteral toStringLiteral() {
		return new StringLiteral("[object Object]");
	}

	@Override
	public BooleanLiteral toBooleanLiteral() {
		return new BooleanLiteral(true);
	}

	@Override
	public NumericLiteral toNumericLiteral() {
		return new NumericLiteral(Double.NaN);
	}

	@Override
	public Function toFunction() throws NotImplemented {
		throw new NotImplemented("Dictionary -> Function");
	}

	@Override
	public Dictionary toDictionary() {
		return this;
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpName(indent, "Dictionary");
		for (Map.Entry<StringLiteral, Value<?>> entry : this.value.entrySet()) {
			System.out.print(entry.getKey().value);
			System.out.print(": ");
			final Value<?> value = entry.getValue();
//			FIXME: Circular dependency
			if (value == this) {
				System.out.println("[[self]]");
			} else {
				entry.getValue().dump(0);
			}
		}
	}
}
