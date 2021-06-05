package xyz.lebster;

import xyz.lebster.exception.LanguageException;
import xyz.lebster.node.ScopeNode;
import xyz.lebster.value.Dictionary;
import xyz.lebster.value.Value;

public class Interpreter {
    public static void dumpIndent(int indent) {
        System.out.print("  ".repeat(indent));
    }

    public final int maxStackSize;
    protected final Dictionary globalObject;
    protected final ScopeFrame[] callStack;
    protected int currentScope = 0;

    public Interpreter(int maxStackSize, Dictionary globalObject) {
        this.maxStackSize = maxStackSize;
        this.globalObject = globalObject;
        this.callStack = new ScopeFrame[maxStackSize];
    }

    public Interpreter(int maxStackSize) {
        this(maxStackSize, new Dictionary());
    }

    public Interpreter(Dictionary globalObject) {
        this(32, globalObject);
    }

    public Interpreter() {
        this(new Dictionary());
    }

    public Value<?> setGlobal(String name, Value<?> value) {
        return this.globalObject.set(name, value);
    }

    public Value<?> getGlobal(String name) {
        return this.globalObject.get(name);
    }

    public ScopeFrame enterScope(ScopeNode node) throws LanguageException {
        if (currentScope + 1 == maxStackSize) {
            throw new LanguageException("Maximum call stack size exceeded");
        }

        final ScopeFrame frame = new ScopeFrame(node);
        callStack[++currentScope] = frame;
        return frame;
    }

    public ScopeFrame exitScope(ScopeNode node) throws LanguageException {
        if (currentScope == 0) {
            throw new LanguageException("Exiting scope while at top level");
        } else if (callStack[currentScope].node != node) {
            throw new LanguageException("Attempting to exit invalid scope");
        }

        currentScope--;
        final ScopeFrame frame = callStack[currentScope];
        callStack[currentScope] = null;
        return frame;
    }

    public void doReturn(Value<?> value) throws LanguageException {
        if (currentScope == 0) {
            throw new LanguageException("Invalid return statement");
        }

        callStack[currentScope].doReturn(value);
    }
}
