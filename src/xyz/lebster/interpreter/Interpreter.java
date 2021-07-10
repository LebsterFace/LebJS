package xyz.lebster.interpreter;

import xyz.lebster.node.BlockStatement;
import xyz.lebster.node.Program;
import xyz.lebster.node.expression.Identifier;
import xyz.lebster.node.value.Dictionary;
import xyz.lebster.node.value.StringLiteral;
import xyz.lebster.node.value.Undefined;
import xyz.lebster.node.value.Value;
import xyz.lebster.runtime.ExecutionError;
import xyz.lebster.runtime.LexicalEnvironment;
import xyz.lebster.runtime.RangeError;

public class Interpreter {
	public final int stackSize;
	private final ExecutionContext[] executionContextStack;
	private int currentExecutionContext = 0;

	public Interpreter(int stackSize, Program program, Dictionary globalObject) {
		this.stackSize = stackSize;
		this.executionContextStack = new ExecutionContext[stackSize];
		this.executionContextStack[0] = new ExecutionContext(new LexicalEnvironment(globalObject, null), null, globalObject);
	}

	public Interpreter(Program program, Dictionary globalObject) {
		this(32, program, globalObject);
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
		return new Undefined();
	}

	public ScopeFrame enterScope(BlockStatement body) throws AbruptCompletion {
		if (currentScopeFrame + 1 == callStackSize) {
			throw new AbruptCompletion(new RangeError("Maximum call stack size exceeded"), AbruptCompletion.Type.Throw);
		}

//		FIXME: Hoisting happens here
		final ScopeFrame frame = new ScopeFrame(new Dictionary(), body);
		scopeStack[++currentScopeFrame] = frame;
		return frame;
	}

	public void exitScope(ScopeFrame frame) {
		if (currentScopeFrame == 0 || scopeStack[currentScopeFrame] != frame) {
			throw new ExecutionError("Attempting to exit from an invalid ScopeFrame");
		} else {
			scopeStack[currentScopeFrame--] = null;
		}
	}

	public void enterExecutionContext(ExecutionContext frame) throws AbruptCompletion {
		if (currentExecutionContext + 1 == callStackSize) {
			throw new AbruptCompletion(new RangeError("Maximum call stack size exceeded"), AbruptCompletion.Type.Throw);
		}

		executionContextStack[++currentExecutionContext] = frame;
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
}