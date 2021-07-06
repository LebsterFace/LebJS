package xyz.lebster.node;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.interpreter.ScopeFrame;
import xyz.lebster.node.value.Value;

public record TryStatement(BlockStatement body, CatchClause handler) implements Statement {

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "TryStatement");
		Dumper.dumpIndicated(indent + 1, "Body", body);
		Dumper.dumpIndicated(indent + 1, "Handler", handler);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		try {
			return body.execute(interpreter);
		} catch (AbruptCompletion completion) {
			if (completion.type != AbruptCompletion.Type.Throw) throw completion;
			final ScopeFrame scope = interpreter.enterScope(body);
			interpreter.declareVariable(handler.parameter(), completion.value);

			try {
				return handler.body().execute(interpreter);
			} finally {
				interpreter.exitScope(scope);
			}
		}
	}
}