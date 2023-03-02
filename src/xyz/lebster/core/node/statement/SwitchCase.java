package xyz.lebster.core.node.statement;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.Dumpable;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

@NonCompliant
public record SwitchCase(Expression test, Statement... statements) implements Dumpable {
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
		DumpBuilder.begin(indent)
			.selfParameterized(this, test == null ? "Default" : "")
			.hiddenChild("Test", test)
			.children("Statements", statements);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("case ");
		test.represent(representation);
		representation.append(": ");
		for (final Statement child : statements)
			child.represent(representation);
	}
}
