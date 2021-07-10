package xyz.lebster.node;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.node.expression.Identifier;
import xyz.lebster.node.value.Value;
import xyz.lebster.runtime.ExecutionError;

public record CatchClause(Identifier parameter, BlockStatement body) implements Statement {
	@Override
	public void dump(int indent) {
		Dumper.dumpParameterized(indent, "CatchClause", parameter.value());
		body.dump(indent + 1);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) {
		throw new ExecutionError("CatchClause execution is handled by TryStatement");
	}
}