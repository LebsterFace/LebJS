package xyz.lebster.core.expression;

import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.runtime.CallFrame;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.exception.LanguageException;

public interface Expression extends ASTNode {
	default CallFrame toCallFrame(Interpreter interpreter) throws LanguageException {
		return new CallFrame(execute(interpreter), interpreter.thisValue());
	}
}