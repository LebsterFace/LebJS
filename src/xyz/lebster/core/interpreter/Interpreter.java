package xyz.lebster.core.interpreter;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.RangeError;
import xyz.lebster.core.value.error.ReferenceError;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.string.StringValue;

public final class Interpreter {
	public final Intrinsics intrinsics;
	public final GlobalObject globalObject;
	public final int stackSize;
	private final ExecutionContext[] executionContextStack;
	private final Mode mode;
	private int currentExecutionContext = 0;

	public Interpreter() {
		this.intrinsics = new Intrinsics();
		this.globalObject = new GlobalObject(intrinsics);
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
	public void declareVariable(StringValue name, Value<?> value) throws AbruptCompletion {
		final Environment environment = executionContextStack[currentExecutionContext].environment();
		if (environment.hasBinding(name)) {
			// FIXME: This should be a Syntax Error at parse-time
			throw AbruptCompletion.error(new ReferenceError(this, "Identifier '" + name.value + "' has already been declared"));
		} else {
			environment.createBinding(this, name, value);
		}
	}

	public void enterExecutionContext(ExecutionContext context) throws AbruptCompletion {
		if (currentExecutionContext + 1 == stackSize) {
			throw AbruptCompletion.error(new RangeError(this, "Maximum call stack size exceeded"));
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
		final LexicalEnvironment env = new LexicalEnvironment(new ObjectValue(null), lexicalEnvironment());
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
		final LexicalEnvironment env = new LexicalEnvironment(new ObjectValue(null), lexicalEnvironment());
		final ExecutionContext context = new ExecutionContext(env, thisValue());
		this.enterExecutionContext(context);
		return context;
	}

	public Reference getBinding(StringValue name) {
		Environment env = this.lexicalEnvironment();
		while (env != null) {
			// 2. Let exists be ? env.HasBinding(name).
			// 3. If exists is true, then
			if (env.hasBinding(name)) {
				// a. Return the Reference Record { base: env, referencedName: name }.
				return env.getBinding(this, name);
			}

			// 4. Else,
			// a. Let outer be env.[[OuterEnv]].
			env = env.parent();
			// (Recursive call)
		}

		// 1. If env is the value null, then
		// a. Return the Reference Record { base: unresolvable, referencedName: name }.
		return new Reference(null, name);
	}

	private enum Mode { Normal, Strict, Checked }
}