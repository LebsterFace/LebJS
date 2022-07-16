package xyz.lebster.core.node.statement;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.environment.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.Assignable;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.IteratorHelper;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

public record ForOfStatement(Assignable left, Expression right, Statement body) implements Statement {
	@Override
	@NonCompliant
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final IteratorHelper.ObjectIterator iterator = IteratorHelper.getIterator(interpreter, right);

		Value<?> lastValue = Undefined.instance;
		for (IteratorHelper.IteratorResult next = iterator.next(); !next.done; next = iterator.next()) {
			final ExecutionContext context = interpreter.pushNewEnvironment();

			try {
				left.assign(interpreter, next.value);
				try {
					lastValue = body.execute(interpreter);
				} catch (AbruptCompletion completion) {
					if (completion.type == AbruptCompletion.Type.Continue) continue;
					if (completion.type == AbruptCompletion.Type.Break) break;
					else throw completion;
				}
			} finally {
				interpreter.exitExecutionContext(context);
			}
		}

		return lastValue;
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.child("Left", left)
			.child("Right", right)
			.child("Body", body);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("for (");
		left.represent(representation);
		representation.append(" of ");
		right.represent(representation);
		representation.append(") ");
		body.represent(representation);
	}
}
