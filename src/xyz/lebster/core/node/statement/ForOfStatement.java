package xyz.lebster.core.node.statement;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.interpreter.*;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.node.expression.LeftHandSideExpression;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.IteratorHelper;
import xyz.lebster.core.runtime.value.primitive.Undefined;

// TODO: `for .. in`
public record ForOfStatement(LeftHandSideExpression left, Expression right, Statement body) implements Statement {
	@Override
	@NonCompliant
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Reference left_reference = left.toReference(interpreter);
		final IteratorHelper.ObjectIterator iterator = IteratorHelper.getIterator(interpreter, right);
		final ExecutionContext context = interpreter.pushNewLexicalEnvironment();
		try {
			Value<?> lastValue = Undefined.instance;
			for (IteratorHelper.IteratorResult next = iterator.next(); !next.done; next = iterator.next()) {
				left_reference.putValue(interpreter, next.value);

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
		Dumper.dumpName(indent, "ForOfStatement");
		Dumper.dumpIndicated(indent, "Left", left);
		Dumper.dumpIndicated(indent, "Right", right);
		Dumper.dumpIndicated(indent, "Body", body);
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
