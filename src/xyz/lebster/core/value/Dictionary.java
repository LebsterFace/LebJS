package xyz.lebster.core.value;

import xyz.lebster.core.exception.NotImplementedException;
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

	public Value<?> set(String name, Value<?> value) {
		return this.value.put(new Identifier(name), value);
	}

	public Value<?> get(String name) {
		return this.value.get(new Identifier(name));
	}

	public Value<?> set(Identifier name, Value<?> value) {
		return this.value.put(name, value);
	}

	public Value<?> get(Identifier name) {
		return this.value.get(name);
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
	public Function toFunction() throws NotImplementedException {
		throw new NotImplementedException("Dictionary -> Function");
	}

	@Override
	public Dictionary toDictionary() { return this; }

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.println("Dictionary:");
		for (Map.Entry<Identifier, Value<?>> entry : this.value.entrySet()) {
			System.out.print(entry.getKey());
			System.out.print(": ");
			entry.getValue().dump(0);
		}
	}
}
