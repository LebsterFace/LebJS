package xyz.lebster.core.node.value;

import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.FunctionNode;

public final class Function extends Constructor<FunctionNode> {
	public final ExecutionContext context;

	public Function(FunctionNode code, ExecutionContext context) {
		super(code);
		this.context = context;
	}

	@Override
	protected Value<?> call(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
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

	@Override
	public Dictionary construct(Value<?>[] executedArguments) {
		final Dictionary dictionary = new Dictionary();
		dictionary.set("isThisFake", new BooleanLiteral(true));
		return dictionary;
	}
}