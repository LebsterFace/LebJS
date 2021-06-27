package xyz.lebster.core.node;

import xyz.lebster.core.runtime.AbruptCompletion;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Value;

public record TryStatement(BlockStatement body, CatchClause handler) implements Statement {

	@Override
	public void dump(int indent) {
		Interpreter.dumpName(indent, "TryStatement");
		Interpreter.dumpIndicated(indent + 1, "Body", body);
		Interpreter.dumpIndicated(indent + 1, "Handler", handler);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		try {
			return body.execute(interpreter);
		} catch (AbruptCompletion completion) {
			if (completion.type != AbruptCompletion.Type.Throw) throw completion;
			return handler.execute(interpreter);
		}
	}
}
