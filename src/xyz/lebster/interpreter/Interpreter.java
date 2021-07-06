package xyz.lebster.interpreter;

import xyz.lebster.node.BlockStatement;
import xyz.lebster.node.Program;
import xyz.lebster.node.expression.Identifier;
import xyz.lebster.node.value.Dictionary;
import xyz.lebster.node.value.StringLiteral;
import xyz.lebster.node.value.Undefined;
import xyz.lebster.node.value.Value;
import xyz.lebster.runtime.ExecutionError;
import xyz.lebster.runtime.RangeError;

public class Interpreter {
	public final int callStackSize;
	private final ScopeFrame[] scopeStack;
	private final CallFrame[] callStack;
	private int currentScopeFrame = 0;
	private int currentCallFrame = 0;

	public Interpreter(int callStackSize, Program program, Dictionary globalObject) {
		this.callStackSize = callStackSize;
		this.scopeStack = new ScopeFrame[callStackSize];
		this.scopeStack[0] = new ScopeFrame(globalObject, program.body);
		this.callStack = new CallFrame[callStackSize];
		this.callStack[0] = new CallFrame(null, globalObject);
	}

	public Interpreter(Program program, Dictionary globalObject) {
		this(32, program, globalObject);
	}

	public Reference getReference(Identifier identifier) {
		final StringLiteral name = identifier.stringValue();

		for (int i = currentScopeFrame; i >= 0; i--) {
			if (scopeStack[i].containsVariable(name)) {
//				FIXME: See fixme in Reference.java
				return new Reference(scopeStack[i].variables(), name);
			}
		}

		return new Reference(null, identifier.stringValue()); // Unresolvable reference
	}

	public Value<?> declareVariable(Identifier identifier, Value<?> value) {
		scopeStack[currentScopeFrame].setVariable(identifier.stringValue(), value);
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

	public void enterCallFrame(CallFrame frame) throws AbruptCompletion {
		if (currentCallFrame + 1 == callStackSize) {
			throw new AbruptCompletion(new RangeError("Maximum call stack size exceeded"), AbruptCompletion.Type.Throw);
		}

		callStack[++currentCallFrame] = frame;
	}

	public void exitCallFrame(CallFrame frame) {
		if (currentCallFrame == 0 || callStack[currentCallFrame] != frame) {
			throw new ExecutionError("Attempting to exit from an invalid CallFrame");
		}

		callStack[currentCallFrame--] = null;
	}

	public CallFrame getCallFrame() {
		return callStack[currentCallFrame];
	}

	public Value<?> thisValue() {
		return callStack[currentCallFrame].thisValue();
	}
}
