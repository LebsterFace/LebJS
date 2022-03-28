package xyz.lebster.core.node.statement;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.primitive.Undefined;

@NonCompliant
public record SwitchStatement(Expression discriminant, SwitchCase... cases) implements Statement {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> value = discriminant.execute(interpreter);
		final int matchingCaseIndex = getMatchingCaseIndex(interpreter, value);
		if (matchingCaseIndex == -1) return Undefined.instance;

		Value<?> lastValue = Undefined.instance;
		for (int index = matchingCaseIndex; index < cases.length; index++) {
			final SwitchCase switchCase = cases[index];
			try {
				lastValue = switchCase.executeStatements(interpreter);
			} catch (AbruptCompletion e) {
				if (e.type == AbruptCompletion.Type.Break) {
					break;
				} else {
					throw e;
				}
			}
		}

		return lastValue;
	}

	private int getMatchingCaseIndex(Interpreter interpreter, Value<?> value) throws AbruptCompletion {
		int defaultCase = -1;

		for (int index = 0; index < cases.length; index++) {
			final SwitchCase switchCase = cases[index];
			if (switchCase.test() == null) {
				defaultCase = index;
			} else if (switchCase.matches(interpreter, value)) {
				return index;
			}
		}

		return defaultCase;
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.child("Discriminant", discriminant)
			.children("Cases", cases);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(ANSI.BACKGROUND_BRIGHT_YELLOW);
		representation.append("[SwitchStatement];");
		representation.appendLine(ANSI.RESET);
	}
}