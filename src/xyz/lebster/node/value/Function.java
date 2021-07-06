package xyz.lebster.node.value;

import xyz.lebster.exception.NotImplemented;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.interpreter.ScopeFrame;
import xyz.lebster.node.FunctionNode;

public class Function extends Executable<FunctionNode> {
	public Function(FunctionNode code) {
		super(code);
	}

	@Override
	public Value<?> call(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		final ScopeFrame scope = interpreter.enterScope(code.body);

//		Declare passed arguments as variables
		for (int i = 0; i < arguments.length && i < code.arguments.length; i++) {
			interpreter.declareVariable(code.arguments[i], arguments[i]);
		}

		final Value<?> result = code.body.execute(interpreter);
		interpreter.exitScope(scope);
		return result;
	}

	public StringLiteral toStringLiteral(Interpreter interpreter) {
		throw new NotImplemented("Function -> StringLiteral");
	}

	public BooleanLiteral toBooleanLiteral(Interpreter interpreter) {
		return new BooleanLiteral(true);
	}

	public NumericLiteral toNumericLiteral(Interpreter interpreter) {
		return new NumericLiteral(Double.NaN);
	}

	public Dictionary toDictionary(Interpreter interpreter) {
		throw new NotImplemented("Function -> Dictionary");
	}
}
