package xyz.lebster.core.node.statement;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.primitive.Undefined;


public record DoWhileStatement(Statement body, Expression condition) implements Statement {
	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-do-while-statement")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> result = Undefined.instance;

		do {
			try {
				body.execute(interpreter);
			} catch (AbruptCompletion completion) {
				if (completion.type == AbruptCompletion.Type.Continue) continue;
				else if (completion.type == AbruptCompletion.Type.Break) break;
				else throw completion;
			}
		} while (condition.execute(interpreter).isTruthy(interpreter));

		return result;
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.child("Condition", condition)
			.child("Body", body);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("do ");
		body.represent(representation);
		representation.append(" while (");
		condition.represent(representation);
		representation.appendLine(");");
	}
}