package xyz.lebster.core.node.statement;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.interpreter.*;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.node.expression.LeftHandSideExpression;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.executable.Executable;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.object.ObjectValue.IteratorRecord;
import xyz.lebster.core.runtime.value.primitive.Undefined;

// TODO: `for .. in`
public record ForOfStatement(LeftHandSideExpression left, Expression right, Statement body) implements Statement {
	@Override
	@NonCompliant
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Reference left_reference = left.toReference(interpreter);
		final IteratorRecord record = right.execute(interpreter).toObjectValue(interpreter).getIterator(interpreter);
		if (record == null) {
			final var representation = new StringRepresentation();
			right.represent(representation);
			representation.append(" is not iterable");
			throw AbruptCompletion.error(new TypeError(representation.toString()));
		}

		final ExecutionContext context = interpreter.pushNewLexicalEnvironment();
		try {
			return this.executeLoop(interpreter, record, left_reference);
		} finally {
			interpreter.exitExecutionContext(context);
		}
	}

	private Value<?> executeLoop(Interpreter $, IteratorRecord record, Reference left) throws AbruptCompletion {
		Value<?> lastValue = Undefined.instance;

		while (true) {
			// Call .next()
			final Executable<?> executable = Executable.getExecutable(record.nextMethod());
			final Value<?> nextResult = executable.call($, record.iterator());

			// If the return value of .next() is not a { done: boolean; value: any; }, throw
			if (!(nextResult instanceof ObjectValue next))
				throw AbruptCompletion.error(new TypeError("Iterator result is not an object"));

			// Stop if .done = true
			if (next.get($, Names.done).toBooleanValue($).value) break;

			// Store .value in the left reference
			left.putValue($, next.get($, Names.value));
			try {
				lastValue = body.execute($);
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
