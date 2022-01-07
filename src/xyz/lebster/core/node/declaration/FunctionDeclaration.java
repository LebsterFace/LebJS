package xyz.lebster.core.node.declaration;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.expression.Identifier;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.node.value.object.Function;
import xyz.lebster.core.node.value.Undefined;
import xyz.lebster.core.node.value.Value;

public final class FunctionDeclaration extends FunctionNode implements Declaration {
	public FunctionDeclaration(BlockStatement body, Identifier name, Identifier... arguments) {
		super(body, name, arguments);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		interpreter.declareVariable(name, new Function(this, interpreter.getExecutionContext()));
		return Undefined.instance;
	}
}