package xyz.lebster.node.value;

import xyz.lebster.exception.NotImplemented;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.ExecutionContext;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.node.FunctionNode;

public class Function extends Executable<FunctionNode> {
	public final ExecutionContext context;

	public Function(FunctionNode code, ExecutionContext context) {
		super(code);
		this.context = context;
	}

	@Override
	public Value<?> call(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		interpreter.enterExecutionContext(context);

//		Declare passed arguments as variables
		for (int i = 0; i < arguments.length && i < code.arguments.length; i++) {
			interpreter.declareVariable(code.arguments[i], arguments[i]);
		}

		try {
			return code.body.execute(interpreter);
		} catch (AbruptCompletion e) {
			if (e.type != AbruptCompletion.Type.Return) throw e;
			return e.value;
		} finally {
			interpreter.exitExecutionContext(context);
		}
	}

	public StringLiteral toStringLiteral() {
		throw new NotImplemented("Function -> StringLiteral");
	}

	public BooleanLiteral toBooleanLiteral() {
		return new BooleanLiteral(true);
	}

	public NumericLiteral toNumericLiteral() {
		return new NumericLiteral(Double.NaN);
	}

	public Dictionary toDictionary() {
		return this;
	}
}