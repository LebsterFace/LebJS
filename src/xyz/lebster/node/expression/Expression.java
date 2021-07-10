package xyz.lebster.node.expression;

import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.ExecutionContext;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.node.ASTNode;
import xyz.lebster.node.value.Dictionary;
import xyz.lebster.runtime.LexicalEnvironment;

public interface Expression extends ASTNode {
	default ExecutionContext toExecutionContext(Interpreter interpreter) throws AbruptCompletion {
		final LexicalEnvironment environment = new LexicalEnvironment(new Dictionary(), interpreter.lexicalEnvironment());
		return new ExecutionContext(environment, execute(interpreter), interpreter.thisValue());
	}
}