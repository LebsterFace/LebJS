package xyz.lebster.core.value;

import xyz.lebster.core.Interpreter;

public class StringLiteral extends Value<String> {
	public StringLiteral() {
		super(Type.String, "");
	}

	public StringLiteral(String value) {
		super(Type.String, value);
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpIndent(indent);
		System.out.print("StringLiteral: '");
		System.out.print(value);
		System.out.println("'");
	}
}
