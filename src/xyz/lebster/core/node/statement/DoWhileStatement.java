package xyz.lebster.core.node.statement;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;


public record DoWhileStatement(SourceRange range, Statement body, Expression condition) implements Statement {
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
}