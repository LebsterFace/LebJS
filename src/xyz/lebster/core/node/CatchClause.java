package xyz.lebster.core.node;

import xyz.lebster.core.expression.Identifier;
import xyz.lebster.core.runtime.AbruptCompletion;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.runtime.ScopeFrame;
import xyz.lebster.core.value.Value;

public record CatchClause(Identifier parameter, BlockStatement body) implements Statement {

	@Override
	public void dump(int indent) {
		Interpreter.dumpParameterized(indent, "CatchClause", parameter.value);
		body.dump(indent + 1);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final ScopeFrame scope = interpreter.enterScope(body);
		interpreter.declareVariable(parameter, interpreter.getCompletion().value);
		final Value<?> result = body.execute(interpreter);
		interpreter.exitScope(scope);
		return result;
	}
}
