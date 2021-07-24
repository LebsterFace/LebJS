package xyz.lebster.core.node;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.node.value.Undefined;
import xyz.lebster.core.node.value.Value;


public record DoWhileStatement(Statement body, Expression condition) implements Statement {
	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-do-while-statement")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> result = new Undefined();

		do {
			try {
				body.execute(interpreter);
			} catch (AbruptCompletion completion) {
				if (completion.type == AbruptCompletion.Type.Continue) continue;
				else if (completion.type == AbruptCompletion.Type.Break) break;
				else throw completion;
			}
		} while (condition.execute(interpreter).isTruthy());

		return result;
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "DoWhileStatement");
		Dumper.dumpIndicated(indent, "Body", body);
		Dumper.dumpIndicated(indent, "Condition", condition);
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