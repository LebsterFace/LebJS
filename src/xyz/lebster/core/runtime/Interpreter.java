package xyz.lebster.core.runtime;

import xyz.lebster.core.exception.LReferenceError;
import xyz.lebster.core.exception.LanguageException;
import xyz.lebster.core.node.Identifier;
import xyz.lebster.core.node.Program;
import xyz.lebster.core.node.ScopeNode;
import xyz.lebster.core.value.Dictionary;
import xyz.lebster.core.value.Value;

public class Interpreter {
	public static void dumpIndent(int indent) {
		System.out.print("  ".repeat(indent));
	}

	public final int maxStackSize;
	private final ScopeFrame[] callStack;
	private int currentScope = 0;

	public Interpreter(int maxStackSize, Program program, Dictionary globalObject) {
		this.maxStackSize = maxStackSize;
		this.callStack = new ScopeFrame[maxStackSize];
		this.callStack[0] = new ScopeFrame(program, globalObject);
	}

	public Interpreter(int maxStackSize, Program program) {
		this(maxStackSize, program, new Dictionary());
	}

	public Interpreter(Program program) {
		this(32, program, new Dictionary());
	}

	public Interpreter(Program program, Dictionary globalObject) {
		this(32, program, globalObject);
	}

	public Value<?> declareVariable(Identifier name, Value<?> value) {
		return callStack[currentScope].setVariable(name, value);
	}

	public void dumpVariables(int scope) {
		callStack[scope].dumpVariables(0);
	}

	public void dumpVariables() {
		dumpVariables(currentScope);
	}

	public Value<?> setVariable(Identifier name, Value<?> value) throws LReferenceError {
		for (int i = currentScope; i >= 0; i--) {
			if (callStack[i].containsVariable(name)) {
				return callStack[i].setVariable(name, value);
			}
		}

		throw new LReferenceError("Unknown variable " + name);
	}
	public Value<?> setVariable(String name, Value<?> value) throws LReferenceError {
		return this.setVariable(new Identifier(name), value);
	}

	public Value<?> getVariable(Identifier name) throws LReferenceError {
		for (int i = currentScope; i >= 0; i--) {
			if (callStack[i].containsVariable(name)) {
				return callStack[i].getVariable(name);
			}
		}

		throw new LReferenceError("Unknown variable " + name);
	}
	public Value<?> getVariable(String name) throws LReferenceError {
		return this.getVariable(new Identifier(name));
	}

	public Value<?> setGlobal(Identifier name, Value<?> value) {
		return callStack[0].setVariable(name, value);
	}
	public Value<?> setGlobal(String name, Value<?> value) {
		return this.setGlobal(new Identifier(name), value);
	}

	public Value<?> getGlobal(Identifier name) {
		return callStack[0].getVariable(name);
	}
	public Value<?> getGlobal(String name) {
		return this.getGlobal(new Identifier(name));
	}

	public ScopeFrame enterScope(ScopeNode node) throws LanguageException {
		if (currentScope + 1 == maxStackSize) {
			throw new LanguageException("Maximum call stack size exceeded");
		}

		final ScopeFrame frame = new ScopeFrame(node);
		callStack[++currentScope] = frame;
		return frame;
	}

	@SuppressWarnings("UnusedReturnValue")
	public ScopeFrame exitScope(ScopeNode node) throws LanguageException {
		if (currentScope == 0) {
			throw new LanguageException("Exiting scope while at top level");
		} else if (callStack[currentScope].node != node) {
			throw new LanguageException("Attempting to exit invalid scope");
		}

		final ScopeFrame frame = callStack[currentScope];
		callStack[currentScope] = null;
		currentScope--;
		return frame;
	}

	public void doExit(Value<?> value) throws LanguageException {
		if (currentScope == 0) {
			throw new LanguageException("Invalid return statement");
		}

		callStack[currentScope].doExit(value);
	}
}
