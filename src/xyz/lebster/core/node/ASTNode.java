package xyz.lebster.core.node;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.value.Value;

public interface ASTNode extends Dumpable {
	Value<?> execute(Interpreter interpreter) throws AbruptCompletion;
}