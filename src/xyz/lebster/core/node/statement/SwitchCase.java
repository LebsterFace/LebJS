package xyz.lebster.core.node.statement;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.primitive.Undefined;

@NonCompliant
public record SwitchCase(Expression test, Statement... statements) {
	public boolean matches(Interpreter interpreter, Value<?> value) throws AbruptCompletion {
		return test.execute(interpreter).equals(value);
	}

	public Value<?> executeStatements(Interpreter interpreter) throws AbruptCompletion {
		Value<?> lastValue = Undefined.instance;
		for (final Statement statement : statements) {
			lastValue = statement.execute(interpreter);
		}

		return lastValue;
	}

	public void dump(int indent) {
		if (test == null) {
			Dumper.dumpName(indent, "Default");
		} else {
			Dumper.dumpName(indent, "Case");
			Dumper.dumpIndicated(indent, "Test", test);
		}
		Dumper.dumpIndicator(indent, "Statements");
		for (final Statement child : statements) {
			child.dump(indent + 1);
		}
	}
}
