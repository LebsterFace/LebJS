package xyz.lebster.core.node;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

public interface FunctionNode extends ASTNode {
	Expression name();

	FunctionParameters parameters();

	BlockStatement body();

	default Value<?> executeBody(Interpreter interpreter, Value<?>[] passedArguments) throws AbruptCompletion {
		try {
			parameters().declareArguments(interpreter, passedArguments);
			body().executeWithoutNewContext(interpreter);
			return Undefined.instance;
		} catch (AbruptCompletion e) {
			if (e.type != AbruptCompletion.Type.Return) throw e;
			return e.value;
		}
	}
}