package xyz.lebster.core.node.statement;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.interpreter.*;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.node.expression.LeftHandSideExpression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.IteratorHelper;
import xyz.lebster.core.value.globals.Undefined;

public record ForOfStatement(LeftHandSideExpression left, Expression right, Statement body) implements Statement {
	@Override
	@NonCompliant
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final IteratorHelper.ObjectIterator iterator = IteratorHelper.getIterator(interpreter, right);
		final ExecutionContext context = interpreter.pushNewLexicalEnvironment();
		final Reference left_reference = left.toReference(interpreter);
		try {
			Value<?> lastValue = Undefined.instance;
			for (IteratorHelper.IteratorResult next = iterator.next(); !next.done; next = iterator.next()) {
				left_reference.putValue(interpreter, next.value); // FIXME: Close the iterator if this errors

				try {
					lastValue = body.execute(interpreter);
				} catch (AbruptCompletion completion) {
					if (completion.type == AbruptCompletion.Type.Continue) continue;
					if (completion.type == AbruptCompletion.Type.Break) break;
					else throw completion;
				}
			}

			return lastValue;
		} finally {
			interpreter.exitExecutionContext(context);
		}
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
