package xyz.lebster.core.value;

import xyz.lebster.exception.NotImplemented;
import xyz.lebster.core.node.Identifier;
import xyz.lebster.core.runtime.Interpreter;

import java.util.HashMap;
import java.util.Map;

public class Dictionary extends Value<HashMap<Identifier, Value<?>>> {
	public Dictionary() {
		super(Type.Dictionary, new HashMap<>());
	}

	public Dictionary(HashMap<Identifier, Value<?>> value) {
		super(Type.Dictionary, value);
	}

	public Value<?> set(Identifier name, Value<?> value) {
		return this.value.put(name, value);
	}

	@SuppressWarnings("UnusedReturnValue")
	public Value<?> set(String name, Value<?> value) {
		return set(new Identifier(name), value);
	}

	public Value<?> get(Identifier name) {
		Dictionary object = this;

		while (true) {
			if (object == null) {
				// End of prototype chain; property does not exist.
				return new Undefined();
			} else if (object.containsKey(name)) {
				// Property was found
				return object.value.get(name);
			} else {
				// Property does not exist on current object. Move up prototype chain
				object = object.getPrototype();
			}
		}
	}

	public boolean containsKey(Identifier name) {
		return this.value.containsKey(name);
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
		Interpreter.dumpIndent(indent);
		System.out.println("Dictionary:");
		for (Map.Entry<Identifier, Value<?>> entry : this.value.entrySet()) {
			System.out.print(entry.getKey());
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
