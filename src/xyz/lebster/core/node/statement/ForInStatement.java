package xyz.lebster.core.node.statement;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.interpreter.*;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.node.expression.LeftHandSideExpression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.string.StringValue;

import java.util.ArrayList;

public record ForInStatement(LeftHandSideExpression left, Expression right, Statement body) implements Statement {
	@Override
	@NonCompliant
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> exprValue = right.execute(interpreter);
		if (exprValue.isNullish()) return Undefined.instance;
		final ObjectValue objectValue = exprValue.toObjectValue(interpreter);
		final ArrayList<StringValue> enumerateProperties = objectValue.enumerateObjectProperties();

		final ExecutionContext context = interpreter.pushNewLexicalEnvironment();
		final Reference left_reference = left.toReference(interpreter);

		try {
			Value<?> lastValue = Undefined.instance;
			for (final StringValue nextResult : enumerateProperties) {
				if (!objectValue.hasOwnEnumerableProperty(nextResult)) continue;

				// TODO: Specifically "initialise" BindingPatterns
				left_reference.putValue(interpreter, nextResult);

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
		representation.append(" in ");
		right.represent(representation);
		representation.append(") ");
		body.represent(representation);
	}
}
