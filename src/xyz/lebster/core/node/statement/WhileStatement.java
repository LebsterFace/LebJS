package xyz.lebster.core.node.statement;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;


public record WhileStatement(Expression condition, Statement body) implements Statement {
	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#prod-WhileStatement")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> result = Undefined.instance;

		while (condition.execute(interpreter).isTruthy(interpreter)) {
			try {
				body.execute(interpreter);
			} catch (AbruptCompletion completion) {
				if (completion.type == AbruptCompletion.Type.Continue) continue;
				else if (completion.type == AbruptCompletion.Type.Break) break;
				else throw completion;
			}
		}

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
		representation.append("while (");
		condition.represent(representation);
		representation.append(") ");
		body.represent(representation);
	}
}