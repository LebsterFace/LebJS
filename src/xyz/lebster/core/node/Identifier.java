package xyz.lebster.core.node;

import xyz.lebster.exception.LanguageException;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Value;

import java.util.Objects;

public class Identifier extends Expression {
	public final String value;

	public Identifier(String name) {
		this.value = name;
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.print("Identifier: '");
		System.out.print(value);
		System.out.println("'");
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws LanguageException {
		return interpreter.getVariable(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Identifier that = (Identifier) o;
		return Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public String toString() {
		return "'" + value + "'";
	}
}