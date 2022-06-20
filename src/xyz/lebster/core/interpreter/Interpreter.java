package xyz.lebster.core.interpreter;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.node.declaration.VariableDeclaration;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.ReferenceError;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.string.StringValue;

import java.util.ArrayDeque;

public final class Interpreter {
	public final Intrinsics intrinsics;
	public final GlobalObject globalObject;
	public final int stackSize;
	private final ArrayDeque<ExecutionContext> executionContextStack = new ArrayDeque<>();
	private final Mode mode;

	public Interpreter() {
		this.intrinsics = new Intrinsics();
		this.globalObject = new GlobalObject(intrinsics);
		this.stackSize = 32;
		executionContextStack.addFirst(new ExecutionContext(new GlobalEnvironment(globalObject), globalObject));
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
	public void declareVariable(String name, Value<?> value) throws AbruptCompletion {
		this.declareVariable(VariableDeclaration.Kind.Let, new StringValue(name), value);
	}

	@NonStandard
	// FIXME: Environment records
	public void declareVariable(VariableDeclaration.Kind kind, StringValue name, Value<?> value) throws AbruptCompletion {
		final Environment environment = executionContextStack.getFirst().environment();
		if (environment.hasBinding(name)) {
			// FIXME: This should be a Syntax Error at parse-time
			throw AbruptCompletion.error(new ReferenceError(this, "Identifier '" + name.value + "' has already been declared"));
		} else {
			environment.createBinding(this, name, value);
		}
	}

	public void enterExecutionContext(ExecutionContext context) {
		executionContextStack.addFirst(context);
	}

	public void exitExecutionContext(ExecutionContext context) {
		if (executionContextStack.size() == 0 || executionContextStack.getFirst() != context) {
			throw new ShouldNotHappen("Attempting to exit from an invalid ExecutionContext");
		}

		executionContextStack.removeFirst();
	}

	public Value<?> thisValue() {
		return executionContextStack.getFirst().thisValue();
	}

	public Environment lexicalEnvironment() {
		return executionContextStack.getFirst().environment();
	}

	public ExecutionContext executionContext() {
		return executionContextStack.getFirst();
	}


	public ExecutionContext pushEnvironmentAndThisValue(Environment env, Value<?> thisValue) {
		final ExecutionContext context = new ExecutionContext(env, thisValue);
		this.enterExecutionContext(context);
		return context;
	}

	public ExecutionContext pushThisValue(Value<?> thisValue) {
		final LexicalEnvironment env = new LexicalEnvironment(new ObjectValue(null), lexicalEnvironment());
		final ExecutionContext context = new ExecutionContext(env, thisValue);
		this.enterExecutionContext(context);
		return context;
	}

	public ExecutionContext pushLexicalEnvironment(Environment env) {
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