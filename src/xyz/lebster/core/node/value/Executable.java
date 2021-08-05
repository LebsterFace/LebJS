package xyz.lebster.core.node.value;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;

import java.util.List;

public abstract class Executable<JType> extends Dictionary {
	// public static Identifier name = new Identifier("name");
	public final JType code;

	public Executable(JType code) {
		super();
		this.code = code;
	}

	public Value<?> callWithContext(Interpreter interpreter, ExecutionContext frame, Value<?>... args) throws AbruptCompletion {
		interpreter.enterExecutionContext(frame);
		try {
			return this.call(interpreter, args);
		} finally {
			interpreter.exitExecutionContext(frame);
		}
	}

	protected abstract Value<?> call(final Interpreter interpreter, final Value<?>... arguments) throws AbruptCompletion;

	@Override
	public void dump(int indent) {
		Dumper.dumpValue(indent, getClass().getSimpleName(), toString());
	}

	@Override
	protected void dumpRecursive(int indent, List<Dictionary> parents) {
		dump(indent);
	}

	@Override
	public String typeOf() {
		return "function";
	}
}