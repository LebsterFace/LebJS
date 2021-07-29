package xyz.lebster.core.node.expression;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.node.value.Dictionary;
import xyz.lebster.core.runtime.LexicalEnvironment;

public interface Expression extends ASTNode {
	default ExecutionContext toExecutionContext(Interpreter interpreter) throws AbruptCompletion {
		final LexicalEnvironment environment = new LexicalEnvironment(new Dictionary(), interpreter.lexicalEnvironment());
		return new ExecutionContext(environment, execute(interpreter), interpreter.thisValue());
	}
}