package xyz.lebster.core.node.statement;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.environment.ExecutionContext;
import xyz.lebster.core.node.Assignable;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.util.ArrayList;

public record ForInStatement(SourceRange range, Assignable left, Expression right, Statement body) implements Statement {
	@Override
	@NonCompliant
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> exprValue = right.execute(interpreter);
		if (exprValue.isNullish()) return Undefined.instance;
		final ObjectValue objectValue = exprValue.toObjectValue(interpreter);
		final ArrayList<StringValue> enumerateProperties = objectValue.enumerateObjectProperties();

		Value<?> lastValue = Undefined.instance;
		for (final StringValue nextResult : enumerateProperties) {
			final ExecutionContext context = interpreter.pushContextWithNewEnvironment();
			try {
				if (!objectValue.hasOwnEnumerableProperty(nextResult)) continue;

				// TODO: Specifically "initialise" BindingPatterns
				left.assign(interpreter, nextResult);
				try {
					lastValue = body.execute(interpreter);
				} catch (AbruptCompletion completion) {
					if (completion.type == AbruptCompletion.Type.Continue) continue;
					if (completion.type == AbruptCompletion.Type.Break) break;
					else throw completion;
				}
			} finally {
				interpreter.exitExecutionContext(context);
			}
		}

		return lastValue;
	}
}
