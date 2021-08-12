package xyz.lebster.core.node.statement;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.expression.Identifier;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.runtime.ExecutionError;

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

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("catch (");
		representation.append(parameter.value());
		representation.append(") ");
		body.represent(representation);
	}
}