package xyz.lebster.core.node.value;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;

import java.util.Set;

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
			return this.internalCall(interpreter, args);
		} finally {
			interpreter.exitExecutionContext(frame);
		}
	}

	public Value<?> call(Interpreter interpreter, Value<?> thisValue, Value<?>... args) throws AbruptCompletion {
		final ExecutionContext context = new ExecutionContext(interpreter.lexicalEnvironment(), this, thisValue);
		interpreter.enterExecutionContext(context);
		try {
			return this.internalCall(interpreter, args);
		} finally {
			interpreter.exitExecutionContext(context);
		}
	}

	protected abstract Value<?> internalCall(final Interpreter interpreter, final Value<?>... arguments) throws AbruptCompletion;

	@Override
	public void dump(int indent) {
		Dumper.dumpValue(indent, getClass().getSimpleName(), toString());
	}

	@Override
	protected void dumpRecursive(int indent, Set<Dictionary> parents) {
		dump(indent);
	}

	@Override
	public String typeOf(Interpreter interpreter) {
		return "function";
	}
}