package xyz.lebster.core.node.statement;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.interpreter.environment.ExecutionContext;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

public record ForStatement(Statement init, Expression test, Expression update, Statement body) implements Statement {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final ExecutionContext context = interpreter.pushNewEnvironment();
		try {
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
		} finally {
			interpreter.exitExecutionContext(context);
		}
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("for (");
		if (init == null) representation.append(";");
		else init.represent(representation);
		representation.append(' ');
		if (test != null) test.represent(representation);
		representation.append("; ");
		if (update != null) update.represent(representation);
		representation.append(") ");
		body.represent(representation);
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.optional("Init", init)
			.optional("Test", test)
			.optional("Update", update)
			.child("Body", body);
	}
}