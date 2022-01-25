package xyz.lebster.core.node.statement;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.Value;

public record CatchClause(String parameter, BlockStatement body) implements Statement {
	@Override
	public void dump(int indent) {
		Dumper.dumpParameterized(indent, "CatchClause", parameter);
		body.dump(indent + 1);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) {
		throw new ShouldNotHappen("CatchClause execution is handled by TryStatement");
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("catch (");
		representation.append(parameter);
		representation.append(") ");
		body.represent(representation);
	}
}