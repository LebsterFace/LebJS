package xyz.lebster.node.expression;

import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.ExecutionContext;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.node.ASTNode;

public interface Expression extends ASTNode {
	default ExecutionContext toExecutionContext(Interpreter interpreter) throws AbruptCompletion {
		return new ExecutionContext(execute(interpreter), interpreter.thisValue());
	}
}