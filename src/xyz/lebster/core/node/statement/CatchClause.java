package xyz.lebster.core.node.statement;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Value;

public record CatchClause(String parameter, BlockStatement body) implements Statement {
	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.selfNamed(this, parameter)
			.container(body);
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