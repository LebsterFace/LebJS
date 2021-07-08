package xyz.lebster.node;

import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.node.expression.Identifier;
import xyz.lebster.node.value.Function;
import xyz.lebster.node.value.Value;

public class FunctionDeclaration extends FunctionNode implements Declaration {
	public FunctionDeclaration(BlockStatement body, Identifier name, Identifier... arguments) {
		super(body, name, arguments);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) {
		return interpreter.declareVariable(name, new Function(this));
	}
}
