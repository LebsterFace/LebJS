package xyz.lebster.core.runtime;

import xyz.lebster.ANSI;
import xyz.lebster.core.expression.Identifier;
import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.node.Program;
import xyz.lebster.core.node.ScopeNode;
import xyz.lebster.core.value.Dictionary;
import xyz.lebster.core.value.Value;
import xyz.lebster.exception.LanguageException;


public class Interpreter {
	public final int maxScopeFrames;
	public final int maxCallFrames;
	private final ScopeFrame[] scopeStack;
	private final CallFrame[] callStack;
	private int currentScopeFrame = 0;
	private int currentCallFrame = 0;
	private AbruptCompletion completion;

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

	public static void dumpName(int indent, String name) {
		dumpIndent(indent);
		System.out.printf("%s%s%s:%n", ANSI.BRIGHT_GREEN, name, ANSI.RESET);
	}

	public static void dumpParameterized(int indent, String name, String param) {
		dumpIndent(indent);
		System.out.printf("%s%s %s%s%s:%n", ANSI.BRIGHT_GREEN, name, ANSI.BRIGHT_YELLOW, param, ANSI.RESET);
	}

	public static void dumpValue(int indent, String name, String value) {
		dumpIndent(indent);
		System.out.printf("%s%s %s%s%s%n", ANSI.CYAN, name, ANSI.BRIGHT_YELLOW, value, ANSI.RESET);
	}

	public static void dumpValue(int indent, String value) {
		dumpIndent(indent);
		System.out.printf("%s%s%s%n", ANSI.CYAN, value, ANSI.RESET);
	}

	public static void dumpSingle(int indent, String value) {
		dumpIndent(indent);
		System.out.printf("%s%s%s;", ANSI.BRIGHT_GREEN, value, ANSI.RESET);
	}

	public static void dumpEnum(int indent, String type, String value) {
		dumpIndent(indent);
		System.out.printf("%s(%s) %s%s%n", ANSI.BRIGHT_MAGENTA, type, value, ANSI.RESET);
	}

	public static void dumpIndicated(int indent, String indicator, ASTNode node) {
		dumpIndent(indent);
		System.out.printf("%s(%s)%s%n", ANSI.BRIGHT_MAGENTA, indicator, ANSI.RESET);
		node.dump(indent + 1);
	}

	public Value<?> declareVariable(Identifier name, Value<?> value) {
		return scopeStack[currentScopeFrame].setVariable(name, value);
	}

	public Value<?> setVariable(Identifier name, Value<?> value) {
		for (int i = currentScopeFrame; i >= 1; i--) {
			if (scopeStack[i].containsVariable(name)) {
				return scopeStack[i].setVariable(name, value);
			}
		}

		return scopeStack[0].setVariable(name, value);
	}

	public Value<?> getVariable(Identifier name) {
		for (int i = currentScopeFrame; i >= 0; i--) {
			if (scopeStack[i].containsVariable(name)) {
				return scopeStack[i].getVariable(name);
			}
		}

		throw new LanguageException(name.value + " is not defined");
	}

	public Value<?> setGlobal(Identifier name, Value<?> value) {
		return scopeStack[0].setVariable(name, value);
	}

	public Value<?> getGlobal(Identifier name) {
		return scopeStack[0].getVariable(name);
	}

	public ScopeFrame enterScope(ScopeNode node) {
		if (currentScopeFrame + 1 == maxScopeFrames) {
			throw new LanguageException("Maximum call stack size exceeded");
		}

		final ScopeFrame frame = new ScopeFrame(node);
		scopeStack[++currentScopeFrame] = frame;
		return frame;
	}

	public void exitScope(ScopeFrame frame) {
		if (currentScopeFrame == 0) {
			throw new LanguageException("Exiting ScopeFrame while at top level");
		} else if (scopeStack[currentScopeFrame] != frame) {
			throw new LanguageException("Attempting to exit invalid ScopeFrame");
		}

		scopeStack[currentScopeFrame--] = null;
	}

	public void enterCallFrame(CallFrame frame) {
		if (currentCallFrame + 1 == maxCallFrames) {
			throw new LanguageException("Maximum call stack size exceeded");
		}

		callStack[++currentCallFrame] = frame;
	}

	public void exitCallFrame() {
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

	public AbruptCompletion getCompletion() {
		return completion;
	}

	public void setCompletion(AbruptCompletion completion) throws AbruptCompletion {
		this.completion = completion;
		throw completion;
	}
}
