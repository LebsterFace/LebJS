package xyz.lebster.core.runtime;

import xyz.lebster.exception.ReferenceError;
import xyz.lebster.exception.LanguageException;
import xyz.lebster.core.node.Identifier;
import xyz.lebster.core.node.Program;
import xyz.lebster.core.node.ScopeNode;
import xyz.lebster.core.value.Dictionary;
import xyz.lebster.core.value.Value;

public class Interpreter {
	public final int maxScopeFrames;
	public final int maxCallFrames;
	private final ScopeFrame[] scopeStack;
	private final CallFrame[] callStack;
	private int currentScopeFrame = 0;
	private int currentCallFrame = 0;

	public Interpreter(Program program, Dictionary globalObject, int maxScopeFrames, int maxCallFrames) {
		this.maxScopeFrames = maxScopeFrames;
		this.maxCallFrames = maxCallFrames;
		this.scopeStack = new ScopeFrame[maxScopeFrames];
		this.callStack = new CallFrame[maxCallFrames];
		scopeStack[0] = new ScopeFrame(program, globalObject);
		callStack[0] = new CallFrame(null, globalObject);
	}

	public Interpreter(Program program, Dictionary globalObject) {
		this(program, globalObject, 120, 32);
	}

	public Interpreter(Program program, int maxScopeFrames, int maxCallFrames) {
		this(program, new Dictionary(), maxScopeFrames, maxCallFrames);
	}

	public Interpreter(Program program) {
		this(program, new Dictionary());
	}

	public static void dumpIndent(int indent) {
		System.out.print("  ".repeat(indent));
	}

	public Value<?> declareVariable(Identifier name, Value<?> value) {
		return scopeStack[currentScopeFrame].setVariable(name, value);
	}

	public void dumpVariables(int scope) {
		scopeStack[scope].dumpVariables(0);
	}

	public void dumpVariables() {
		dumpVariables(currentScopeFrame);
	}

	public Value<?> setVariable(Identifier name, Value<?> value) throws ReferenceError {
		for (int i = currentScopeFrame; i >= 0; i--) {
			if (scopeStack[i].containsVariable(name)) {
				return scopeStack[i].setVariable(name, value);
			}
		}

		throw new ReferenceError("Unknown variable " + name);
	}

	public Value<?> setVariable(String name, Value<?> value) throws ReferenceError {
		return this.setVariable(new Identifier(name), value);
	}

	public Value<?> getVariable(Identifier name) throws ReferenceError {
		for (int i = currentScopeFrame; i >= 0; i--) {
			if (scopeStack[i].containsVariable(name)) {
				return scopeStack[i].getVariable(name);
			}
		}

		throw new ReferenceError(name.value + " is not defined");
	}

	public Value<?> getVariable(String name) throws ReferenceError {
		return this.getVariable(new Identifier(name));
	}

	public Value<?> setGlobal(Identifier name, Value<?> value) {
		return scopeStack[0].setVariable(name, value);
	}

	public Value<?> setGlobal(String name, Value<?> value) {
		return this.setGlobal(new Identifier(name), value);
	}

	public Value<?> getGlobal(Identifier name) {
		return scopeStack[0].getVariable(name);
	}

	public Value<?> getGlobal(String name) {
		return this.getGlobal(new Identifier(name));
	}

	public ScopeFrame enterScope(ScopeNode node) throws LanguageException {
		if (currentScopeFrame + 1 == maxScopeFrames) {
			throw new LanguageException("Maximum call stack size exceeded");
		}

		final ScopeFrame frame = new ScopeFrame(node);
		scopeStack[++currentScopeFrame] = frame;
		return frame;
	}

	public void exitScope(ScopeFrame frame) throws LanguageException {
		if (currentScopeFrame == 0) {
			throw new LanguageException("Exiting ScopeFrame while at top level");
		} else if (scopeStack[currentScopeFrame] != frame) {
			throw new LanguageException("Attempting to exit invalid ScopeFrame");
		}

		scopeStack[currentScopeFrame--] = null;
	}

	public Value<?> doExit(Value<?> value) throws LanguageException {
		if (currentScopeFrame == 0) {
			throw new LanguageException("Invalid return statement");
		}

		scopeStack[currentScopeFrame].doExit(value);
		return value;
	}

	public void enterCallFrame(CallFrame frame) throws LanguageException {
		if (currentCallFrame + 1 == maxCallFrames) {
			throw new LanguageException("Maximum call stack size exceeded");
		}

		callStack[++currentCallFrame] = frame;
	}

	public void exitCallFrame() throws LanguageException {
		if (currentCallFrame == 0) {
			throw new LanguageException("Exiting CallFrame while at top level");
		}

		callStack[currentCallFrame--] = null;
	}

	public CallFrame getCallFrame() {
		return callStack[currentCallFrame];
	}

	public Value<?> thisValue() {
		return getCallFrame().thisValue();
	}
}
