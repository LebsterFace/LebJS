package xyz.lebster.node.value;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.node.expression.Identifier;

import java.util.List;

public abstract class Executable<JType> extends Dictionary {
	public static Identifier name = new Identifier("name");
	public final JType code;

	public Executable(JType code) {
		super();
		this.code = code;
	}

	public abstract Value<?> call(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion;

	@Override
	public void dump(int indent) {
		Dumper.dumpValue(indent, getClass().getSimpleName(), toString());
	}

	@Override
	protected void dumpRecursive(int indent, List<Dictionary> parents) {
		dump(indent);
	}
}
