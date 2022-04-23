package xyz.lebster.core.node.statement;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.interpreter.*;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.node.expression.LeftHandSideExpression;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.primitive.Undefined;

import java.util.ArrayList;

// FIXME: Properties which are removed while iterating should not appear in the iteration
public record ForInStatement(LeftHandSideExpression left, Expression right, Statement body) implements Statement {
	@Override
	@NonCompliant
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Reference left_reference = left.toReference(interpreter);
		final Value<?> exprValue = right.execute(interpreter);
		if (exprValue.isNullish()) return Undefined.instance;
		final ObjectValue objectValue = exprValue.toObjectValue(interpreter);
		final ArrayList<StringValue> enumerateProperties = objectValue.enumerateObjectProperties();

		final ExecutionContext context = interpreter.pushNewLexicalEnvironment();

		try {
			Value<?> lastValue = Undefined.instance;
			for (final StringValue nextResult : enumerateProperties) {
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
