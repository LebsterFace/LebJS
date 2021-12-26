package xyz.lebster.core.interpreter;

import xyz.lebster.core.node.SpecificationURL;
import xyz.lebster.core.node.expression.Identifier;
import xyz.lebster.core.node.value.Dictionary;
import xyz.lebster.core.node.value.StringLiteral;
import xyz.lebster.core.node.value.Undefined;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.runtime.ExecutionError;
import xyz.lebster.core.runtime.LexicalEnvironment;
import xyz.lebster.core.runtime.RangeError;

public final class Interpreter {
	public final GlobalObject globalObject;
	public final int stackSize;
	private final ExecutionContext[] executionContextStack;
	private int currentExecutionContext = 0;

	public Interpreter(int stackSize, GlobalObject globalObject) {
		this.globalObject = globalObject;
		this.stackSize = stackSize;
		this.executionContextStack = new ExecutionContext[stackSize];
		this.executionContextStack[0] = new ExecutionContext(new LexicalEnvironment(globalObject, null), null, globalObject);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-resolvebinding")
	public Reference getReference(Identifier identifier) {
		final StringLiteral name = identifier.stringValue();
		for (LexicalEnvironment env = lexicalEnvironment(); env != null; env = env.parent()) {
			if (env.hasBinding(name)) {
				return new Reference(env.variables(), name);
			}
		}

		// Unresolvable reference
		return new Reference(null, name);
	}

	public Value<?> declareVariable(Identifier identifier, Value<?> value) {
		lexicalEnvironment().setVariable(identifier.stringValue(), value);
//		FIXME: Errors can technically be thrown here, so don't always return a normal completion
		return Undefined.instance;
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
		final LexicalEnvironment env = new LexicalEnvironment(new Dictionary(), lexicalEnvironment());
		final ExecutionContext context = new ExecutionContext(env, null, thisValue);
		enterExecutionContext(context);
		return context;
	}
}