package xyz.lebster.core.node.statement;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.node.value.Undefined;
import xyz.lebster.core.node.value.Value;

public record ForStatement(Statement init, Expression test, Expression update, Statement body) implements Statement {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		if (init != null) init.execute(interpreter);
		final Value<?> result = Undefined.instance;

		while (test.execute(interpreter).isTruthy(interpreter)) {
			try {
				body.execute(interpreter);
				update.execute(interpreter);
			} catch (AbruptCompletion completion) {
				if (completion.type == AbruptCompletion.Type.Continue) continue;
				else if (completion.type == AbruptCompletion.Type.Break) break;
				else throw completion;
			}
		}

		return result;
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("for (");
		init.represent(representation);
		representation.append(" ");
		test.represent(representation);
		representation.append("; ");
		update.represent(representation);
		representation.append(") ");
		body.represent(representation);
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "ForStatement");
		Dumper.dumpIndicated(indent, "Init", init);
		Dumper.dumpIndicated(indent, "Test", test);
		Dumper.dumpIndicated(indent, "Update", update);
		Dumper.dumpIndicated(indent, "Body", body);
	}
}