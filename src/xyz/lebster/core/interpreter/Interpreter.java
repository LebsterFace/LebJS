package xyz.lebster.core.interpreter;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.RangeError;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;

public final class Interpreter {
	public final GlobalObject globalObject;
	public final int stackSize;
	private final ExecutionContext[] executionContextStack;
	private final Mode mode;
	private int currentExecutionContext = 0;

	public Interpreter() {
		this.globalObject = new GlobalObject();
		this.stackSize = 32;
		this.executionContextStack = new ExecutionContext[stackSize];
		this.executionContextStack[0] = new ExecutionContext(new GlobalEnvironment(globalObject), globalObject);
		this.mode = Mode.Strict;
	}

	public boolean isStrictMode() {
		return this.mode == Mode.Checked || this.mode == Mode.Strict;
	}

	public boolean isCheckedMode() {
		return this.mode == Mode.Checked;
	}

	@NonStandard
	// FIXME: Environment records
	public void declareVariable(String name, Value<?> value) {
		lexicalEnvironment().createBinding(this, new StringValue(name), value);
	}

	@NonStandard
	// FIXME: Environment records
	public void declareVariable(StringValue name, Value<?> value) {
		lexicalEnvironment().createBinding(this, name, value);
	}

	public void enterExecutionContext(ExecutionContext context) throws AbruptCompletion {
		if (currentExecutionContext + 1 == stackSize) {
			throw AbruptCompletion.error(new RangeError("Maximum call stack size exceeded"));
		}

		executionContextStack[++currentExecutionContext] = context;
	}

	public void exitExecutionContext(ExecutionContext frame) {
		if (currentExecutionContext == 0 || executionContextStack[currentExecutionContext] != frame) {
			throw new ShouldNotHappen("Attempting to exit from an invalid ExecutionContext");
		}

		executionContextStack[currentExecutionContext--] = null;
	}

	public Value<?> thisValue() {
		return executionContextStack[currentExecutionContext].thisValue();
	}

	public Environment lexicalEnvironment() {
		return executionContextStack[currentExecutionContext].environment();
	}

	public ExecutionContext executionContext() {
		return executionContextStack[currentExecutionContext];
	}


	public ExecutionContext pushEnvironmentAndThisValue(Environment env, Value<?> thisValue) throws AbruptCompletion {
		final ExecutionContext context = new ExecutionContext(env, thisValue);
		this.enterExecutionContext(context);
		return context;
	}

	public ExecutionContext pushThisValue(Value<?> thisValue) throws AbruptCompletion {
		final LexicalEnvironment env = new LexicalEnvironment(new ObjectValue(), lexicalEnvironment());
		final ExecutionContext context = new ExecutionContext(env, thisValue);
		this.enterExecutionContext(context);
		return context;
	}

	public ExecutionContext pushLexicalEnvironment(Environment env) throws AbruptCompletion {
		final ExecutionContext context = new ExecutionContext(env, thisValue());
		this.enterExecutionContext(context);
		return context;
	}

	public ExecutionContext pushNewLexicalEnvironment() throws AbruptCompletion {
		final LexicalEnvironment env = new LexicalEnvironment(new ObjectValue(), lexicalEnvironment());
		final ExecutionContext context = new ExecutionContext(env, thisValue());
		this.enterExecutionContext(context);
		return context;
	}

	private enum Mode { Normal, Strict, Checked }
}