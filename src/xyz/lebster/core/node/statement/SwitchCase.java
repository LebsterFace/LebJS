package xyz.lebster.core.node.statement;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

@NonCompliant
public record SwitchCase(Expression test, Statement... statements) {
	public boolean matches(Interpreter interpreter, Value<?> value) throws AbruptCompletion {
		return test.execute(interpreter).isStrictlyEqual(value);
	}

	public Value<?> executeStatements(Interpreter interpreter) throws AbruptCompletion {
		Value<?> lastValue = Undefined.instance;
		for (final Statement statement : statements) {
			lastValue = statement.execute(interpreter);
		}

		return lastValue;
	}
}
