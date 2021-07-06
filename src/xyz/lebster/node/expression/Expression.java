package xyz.lebster.node.expression;

import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.CallFrame;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.node.ASTNode;

public interface Expression extends ASTNode {
	default CallFrame toCallFrame(Interpreter interpreter) throws AbruptCompletion {
		return new CallFrame(execute(interpreter), interpreter.thisValue());
	}
}
