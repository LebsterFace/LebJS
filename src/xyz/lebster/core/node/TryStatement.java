package xyz.lebster.core.node;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.Value;

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
			interpreter.declareVariable(handler.parameter(), completion.value);
			return handler.body().execute(interpreter);
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