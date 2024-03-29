package xyz.lebster.core.node.statement;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.environment.ExecutionContext;
import xyz.lebster.core.node.Assignable;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.iterator.IteratorRecord;

import static xyz.lebster.core.value.iterator.IteratorPrototype.*;

public record ForOfStatement(SourceRange range, Assignable left, Expression right, Statement body) implements Statement {
	@Override
	@NonCompliant
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final IteratorRecord iterator = getIterator(interpreter, right);

		Value<?> lastValue = Undefined.instance;
		var iterResult = iterator.next(interpreter, null);
		while (!iteratorComplete(interpreter, iterResult)) {
			final ExecutionContext context = interpreter.pushContextWithNewEnvironment();

			try {
				left.assign(interpreter, iteratorValue(interpreter, iterResult));
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
}
