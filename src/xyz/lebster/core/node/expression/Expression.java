package xyz.lebster.core.node.expression;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.node.value.Dictionary;
import xyz.lebster.core.node.value.Executable;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.runtime.LexicalEnvironment;
import xyz.lebster.core.runtime.TypeError;

public interface Expression extends ASTNode {
	default ExecutionContext toExecutionContext(Interpreter interpreter) throws AbruptCompletion {
		final LexicalEnvironment environment = new LexicalEnvironment(new Dictionary(), interpreter.lexicalEnvironment());
		return new ExecutionContext(environment, execute(interpreter), interpreter.thisValue());
	}

	default Value<?> callAsExecutable(final Interpreter interpreter, final Value<?>[] args) throws AbruptCompletion {
		final ExecutionContext frame = this.toExecutionContext(interpreter);
		if (frame.executedCallee() instanceof final Executable<?> executable) {
			interpreter.enterExecutionContext(frame);
			try {
				return executable.call(interpreter, args);
			} finally {
				interpreter.exitExecutionContext(frame);
			}
		} else {
			final StringRepresentation representation = new StringRepresentation();
			this.represent(representation);
			representation.append(" is not a function");
			throw AbruptCompletion.error(new TypeError(representation.toString()));
		}
	}
}