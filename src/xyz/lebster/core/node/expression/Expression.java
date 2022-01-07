package xyz.lebster.core.node.expression;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.node.value.object.ObjectLiteral;
import xyz.lebster.core.runtime.LexicalEnvironment;

public interface Expression extends ASTNode {
	default ExecutionContext toExecutionContext(Interpreter interpreter) throws AbruptCompletion {
		final LexicalEnvironment environment = new LexicalEnvironment(new ObjectLiteral(), interpreter.lexicalEnvironment());
		return new ExecutionContext(environment, execute(interpreter), interpreter.thisValue());
	}
}