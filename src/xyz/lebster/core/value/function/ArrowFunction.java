package xyz.lebster.core.value.function;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.interpreter.environment.ExecutionContext;
import xyz.lebster.core.node.expression.ArrowFunctionExpression;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.primitive.string.StringValue;

public final class ArrowFunction extends Executable {
	private final ExecutionContext context;
	private final ArrowFunctionExpression expression;

	public ArrowFunction(Intrinsics intrinsics, ArrowFunctionExpression expression, ExecutionContext context) {
		super(intrinsics, Names.EMPTY, expression.parameters().expectedArgumentCount());
		this.expression = expression;
		this.context = context;
	}

	@Override
	public StringValue toStringMethod() {
		return new StringValue(expression.range().getText());
	}

	private Value<?> executeCode(Interpreter interpreter, Value<?>[] passedArguments) throws AbruptCompletion {
		final ExecutionContext context = interpreter.pushContextWithNewEnvironment();

		try {
			expression.parameters().declareArguments(interpreter, passedArguments);

			if (expression.hasFullBody()) {
				expression.body().executeWithoutNewContext(interpreter);
				return Undefined.instance;
			} else {
				return expression.implicitReturn().execute(interpreter);
			}
		} catch (AbruptCompletion e) {
			if (e.type != AbruptCompletion.Type.Return) throw e;
			return e.value;
		} finally {
			interpreter.exitExecutionContext(context);
		}
	}

	@Override
	public Value<?> internalCall(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		// Arrow functions ignore attempts to bind `this`
		interpreter.enterExecutionContext(this.context);
		try {
			return this.executeCode(interpreter, arguments);
		} finally {
			interpreter.exitExecutionContext(this.context);
		}
	}
}