package xyz.lebster.core.interpreter;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.environment.*;
import xyz.lebster.core.node.declaration.VariableDeclaration;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.range.RangeError;
import xyz.lebster.core.value.error.reference.ReferenceError;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.util.ArrayDeque;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

public final class Interpreter {
	private static final int MAX_CALLSTACK_SIZE = 256;
	public final Intrinsics intrinsics;
	public final GlobalObject globalObject;
	private final ArrayDeque<ExecutionContext> executionContextStack;
	private final Mode mode;

	public Interpreter() {
		this.intrinsics = new Intrinsics();
		this.globalObject = new GlobalObject(intrinsics);
		this.executionContextStack = new ArrayDeque<>();
		this.executionContextStack.addFirst(new ExecutionContext(new GlobalEnvironment(globalObject)));
		this.mode = Mode.Strict;
	}

	public void enterExecutionContext(ExecutionContext context) throws AbruptCompletion {
		if (executionContextStack.size() >= MAX_CALLSTACK_SIZE) {
			throw AbruptCompletion.error(new RangeError(this, "Maximum call stack size exceeded"));
		}

		executionContextStack.addFirst(context);
	}

	public void exitExecutionContext(ExecutionContext context) {
		if (executionContextStack.size() == 0 || executionContextStack.getFirst() != context) {
			throw new ShouldNotHappen("Attempting to exit from an invalid ExecutionContext");
		}

		executionContextStack.removeFirst();
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
		final Environment environment = environment();
		if (environment.hasBinding(name)) {
			// FIXME: This should be a Syntax Error at parse-time
			throw error(new ReferenceError(this, "Identifier '" + name.value + "' has already been declared"));
		} else {
			environment.createBinding(this, name, value);
		}
	}

	public Value<?> thisValue() {
		final FunctionEnvironment thisEnvironment = getThisEnvironment();
		if (thisEnvironment == null) return globalObject;
		return thisEnvironment.thisValue;
	}

	public ObjectValue getNewTarget() {
		final FunctionEnvironment thisEnvironment = getThisEnvironment();
		if (thisEnvironment == null) return null;
		return thisEnvironment.newTarget;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-getsuperconstructor")
	public ObjectValue getSuperConstructor() {
		// 1. Let envRec be GetThisEnvironment().
		final FunctionEnvironment envRec = getThisEnvironment();
		// 2. Assert: envRec is a function Environment Record.
		if (envRec == null) throw new ShouldNotHappen("getSuperConstructor() called when getThisEnvironment() returns null");
		// 3. Let activeFunction be envRec.[[FunctionObject]].
		final Executable activeFunction = envRec.functionObject;
		// 4. Assert: activeFunction is an ECMAScript function object.
		// 5. Let superConstructor be ! activeFunction.[[GetPrototypeOf]]().
		// 6. Return superConstructor.
		return activeFunction.getPrototype();
	}

	public FunctionEnvironment getThisEnvironment() {
		for (final ExecutionContext context : executionContextStack) {
			Environment env = context.environment();
			while (env != null) {
				if (env instanceof final FunctionEnvironment result) {
					return result;
				}

				env = env.parent();
			}
		}

		return null;
	}

	public Environment environment() {
		return executionContextStack.getFirst().environment();
	}

	public ExecutionContext executionContext() {
		return executionContextStack.getFirst();
	}

	public ExecutionContext pushContextWithEnvironment(Environment env) throws AbruptCompletion {
		final ExecutionContext context = new ExecutionContext(env);
		this.enterExecutionContext(context);
		return context;
	}

	public ExecutionContext pushContextWithNewEnvironment() throws AbruptCompletion {
		return this.pushContextWithEnvironment(new DeclarativeEnvironment(environment()));
	}

	public Reference getBinding(StringValue name) {
		Environment env = this.environment();
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