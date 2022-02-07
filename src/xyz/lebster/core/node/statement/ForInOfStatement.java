package xyz.lebster.core.node.statement;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.node.expression.LeftHandSideExpression;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.executable.Executable;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.object.ObjectValue.IteratorRecord;
import xyz.lebster.core.runtime.value.primitive.UndefinedValue;

public record ForInOfStatement(LeftHandSideExpression left, Expression right, Statement body) implements Statement {
	@Override
	@NonCompliant
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Reference left_reference = left.toReference(interpreter);
		final Value<?> right_value = right.execute(interpreter);
		final IteratorRecord record = right_value.toObjectValue(interpreter).getIterator(interpreter);
		Value<?> lastValue = UndefinedValue.instance;

		while (true) {
			final Value<?> nextResult = Executable.getExecutable(record.nextMethod()).call(interpreter, record.iterator());
			if (!(nextResult instanceof ObjectValue next)) throw AbruptCompletion.error(new TypeError("Iterator result is not an object"));
			if (next.get(interpreter, Names.done).toBooleanValue(interpreter).value) break;

			left_reference.putValue(interpreter, next.get(interpreter, Names.value));
			try {
				lastValue = body.execute(interpreter);
			} catch (AbruptCompletion completion) {
				if (completion.type == AbruptCompletion.Type.Continue) continue;
				if (completion.type == AbruptCompletion.Type.Break) break;
				else throw completion;
			}
		}

		return lastValue;
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
