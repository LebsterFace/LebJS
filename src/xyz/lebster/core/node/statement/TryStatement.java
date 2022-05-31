package xyz.lebster.core.node.statement;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Value;

public record TryStatement(BlockStatement body, CatchClause handler) implements Statement {

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.child("Body", body)
			.child("Handler", handler);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		try {
			return body.execute(interpreter);
		} catch (AbruptCompletion completion) {
			if (completion.type != AbruptCompletion.Type.Throw) throw completion;
			final ExecutionContext context = interpreter.pushNewLexicalEnvironment();
			interpreter.declareVariable(handler.parameter(), completion.value);
			try {
				return handler.body().executeWithoutNewContext(interpreter);
			} finally {
				interpreter.exitExecutionContext(context);
			}
		}
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("try ");
		body.represent(representation);
		representation.append(' ');
		handler.represent(representation);
	}
}