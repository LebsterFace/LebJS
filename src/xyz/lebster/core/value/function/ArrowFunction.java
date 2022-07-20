package xyz.lebster.core.value.function;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.environment.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.expression.ArrowFunctionExpression;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.string.StringValue;

public final class ArrowFunction extends Executable {
	private final ExecutionContext context;
	private final ArrowFunctionExpression expression;

	public ArrowFunction(Interpreter interpreter, ArrowFunctionExpression expression, ExecutionContext context) {
		// TODO: Automatic function names for arrow & non-arrow functions
		//		e.g. ' let k = () => {} ' should be given the name 'k'
		super(interpreter.intrinsics.functionPrototype, Names.EMPTY);
		this.expression = expression;
		this.context = context;
	}

	@Override
	public StringValue toStringMethod() {
		return new StringValue(expression.toRepresentationString());
	}

	private Value<?> executeCode(Interpreter interpreter, Value<?>[] passedArguments) throws AbruptCompletion {
		final ExecutionContext context = interpreter.pushNewEnvironment();

		try {
			expression.arguments.declareArguments(interpreter, passedArguments);

			if (expression.hasFullBody) {
				expression.body.executeWithoutNewContext(interpreter);
				return Undefined.instance;
			} else {
				return expression.implicitReturnExpression.execute(interpreter);
			}
		} catch (AbruptCompletion e) {
			if (e.type != AbruptCompletion.Type.Return) throw e;
			return e.value;
		} finally {
			interpreter.exitExecutionContext(context);
		}
	}

	@Override
	public Value<?> call(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		interpreter.enterExecutionContext(this.context);
		try {
			return this.executeCode(interpreter, arguments);
		} finally {
			interpreter.exitExecutionContext(this.context);
		}
	}

	@Override
	public Value<?> call(Interpreter interpreter, Value<?> newThisValue, Value<?>... arguments) throws AbruptCompletion {
		return this.call(interpreter, arguments);
	}
}