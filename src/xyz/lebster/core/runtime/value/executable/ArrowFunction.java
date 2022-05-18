package xyz.lebster.core.runtime.value.executable;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.expression.ArrowFunctionExpression;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.object.ObjectValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.primitive.Undefined;

import java.util.HashSet;

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
	public void displayRecursive(StringRepresentation representation, HashSet<ObjectValue> parents, boolean singleLine) {
		this.display(representation);
	}

	@Override
	public StringValue toStringMethod() {
		return new StringValue(expression.toRepresentationString());
	}

	private Value<?> executeCode(Interpreter interpreter, Value<?>[] passedArguments) throws AbruptCompletion {
		interpreter.enterExecutionContext(this.context);

		// Declare passed arguments as variables
		int i = 0;
		for (; i < expression.arguments.length && i < passedArguments.length; i++)
			interpreter.declareVariable(expression.arguments[i], passedArguments[i]);
		for (; i < expression.arguments.length; i++)
			interpreter.declareVariable(expression.arguments[i], Undefined.instance);

		try {
			if (expression.hasFullBody) {
				expression.body.execute(interpreter);
				return Undefined.instance;
			} else {
				return expression.implicitReturnExpression.execute(interpreter);
			}
		} catch (AbruptCompletion e) {
			if (e.type != AbruptCompletion.Type.Return) throw e;
			return e.value;
		} finally {
			interpreter.exitExecutionContext(this.context);
		}
	}

	@Override
	public Value<?> call(Interpreter interpreter, Value<?>... arguments) throws AbruptCompletion {
		return this.executeCode(interpreter, arguments);
	}

	@Override
	public Value<?> call(Interpreter interpreter, Value<?> newThisValue, Value<?>... arguments) throws AbruptCompletion {
		return this.executeCode(interpreter, arguments);
	}
}