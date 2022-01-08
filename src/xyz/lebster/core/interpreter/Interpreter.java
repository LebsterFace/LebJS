package xyz.lebster.core.interpreter;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.node.expression.Identifier;
import xyz.lebster.core.node.value.object.ObjectValue;
import xyz.lebster.core.node.value.StringValue;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.runtime.error.ExecutionError;
import xyz.lebster.core.runtime.LexicalEnvironment;
import xyz.lebster.core.runtime.error.RangeError;

public final class Interpreter {
	public final GlobalObject globalObject;
	public final int stackSize;
	private final ExecutionContext[] executionContextStack;
	private int currentExecutionContext = 0;

	public Interpreter() {
		this.globalObject = new GlobalObject();
		this.stackSize = 32;
		this.executionContextStack = new ExecutionContext[stackSize];
		this.executionContextStack[0] = new ExecutionContext(new LexicalEnvironment(globalObject, null), null, globalObject);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-resolvebinding")
	public Reference getReference(Identifier identifier) {
		final StringValue name = identifier.stringValue();
		for (LexicalEnvironment env = lexicalEnvironment(); env != null; env = env.parent()) {
			if (env.hasBinding(name)) {
				return new Reference(env.variables(), name);
			}
		}

		// Unresolvable reference
		return new Reference(null, name);
	}

	public void declareVariable(Identifier identifier, Value<?> value) throws AbruptCompletion {
		lexicalEnvironment().setVariable(this, identifier.stringValue(), value);
	}

	public void enterExecutionContext(ExecutionContext context) throws AbruptCompletion {
		if (currentExecutionContext + 1 == stackSize) {
			throw AbruptCompletion.error(new RangeError("Maximum call stack size exceeded"));
		}

		executionContextStack[++currentExecutionContext] = context;
	}

	public void exitExecutionContext(ExecutionContext frame) {
		if (currentExecutionContext == 0 || executionContextStack[currentExecutionContext] != frame) {
			throw new ExecutionError("Attempting to exit from an invalid ExecutionContext");
		}

		executionContextStack[currentExecutionContext--] = null;
	}

	public ExecutionContext getExecutionContext() {
		return executionContextStack[currentExecutionContext];
	}

	public Value<?> thisValue() {
		return executionContextStack[currentExecutionContext].thisValue();
	}

	public LexicalEnvironment lexicalEnvironment() {
		return executionContextStack[currentExecutionContext].environment();
	}

	public ExecutionContext pushExecutionContext(Value<?> thisValue) throws AbruptCompletion {
		final LexicalEnvironment env = new LexicalEnvironment(new ObjectValue(), lexicalEnvironment());
		final ExecutionContext context = new ExecutionContext(env, null, thisValue);
		enterExecutionContext(context);
		return context;
	}
}