package xyz.lebster.core.runtime;

import xyz.lebster.core.expression.Identifier;
import xyz.lebster.core.value.Dictionary;
import xyz.lebster.core.value.StringLiteral;

public class Error extends Dictionary {
	public static final Identifier message = new Identifier("message");
	public static final Identifier name = new Identifier("name");

	public Error(String msg) {
		super.set(message, new StringLiteral(msg));
		super.set(name, new StringLiteral(getClass().getSimpleName()));
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpParameterized(indent, "Error", toString());
	}

	@Override
	public String toString() {
		return getName() + ": " + super.get(message).toStringLiteral().value;
	}

	public String getName() {
		return super.get(name).toStringLiteral().value;
	}
}
