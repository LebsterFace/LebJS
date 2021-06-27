package xyz.lebster.core.expression;

import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.runtime.AbruptCompletion;
import xyz.lebster.core.runtime.CallFrame;
import xyz.lebster.core.runtime.Interpreter;


public interface Expression extends ASTNode {
	default CallFrame toCallFrame(Interpreter interpreter) throws AbruptCompletion {
		return new CallFrame(execute(interpreter), interpreter.thisValue());
	}
}