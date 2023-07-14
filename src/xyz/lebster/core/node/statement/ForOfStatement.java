package xyz.lebster.core.node.statement;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.interpreter.environment.ExecutionContext;
import xyz.lebster.core.node.Assignable;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.IteratorHelper;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

public record ForOfStatement(Assignable left, Expression right, Statement body) implements Statement {
	@Override
	@NonCompliant
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final IteratorHelper.IteratorRecord iterator = IteratorHelper.getIterator(interpreter, right);

		Value<?> lastValue = Undefined.instance;
		var iterResult = iterator.next(interpreter, null);
		while (!IteratorHelper.iteratorComplete(interpreter, iterResult)) {
			final ExecutionContext context = interpreter.pushContextWithNewEnvironment();

			try {
				left.assign(interpreter, IteratorHelper.iteratorValue(interpreter, iterResult));
				try {
					lastValue = body.execute(interpreter);
				} catch (AbruptCompletion completion) {
					if (completion.type == AbruptCompletion.Type.Continue) {
						iterResult = iterator.next(interpreter, null);
						continue;
					}
					if (completion.type == AbruptCompletion.Type.Break) break;
					else throw completion;
				}
			} finally {
				interpreter.exitExecutionContext(context);
			}
			iterResult = iterator.next(interpreter, null);
		}

		return lastValue;
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
