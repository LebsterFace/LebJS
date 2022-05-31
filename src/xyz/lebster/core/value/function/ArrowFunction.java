package xyz.lebster.core.value.function;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.FunctionNode;
import xyz.lebster.core.node.expression.ArrowFunctionExpression;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.string.StringValue;

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
		final ExecutionContext context = interpreter.pushNewLexicalEnvironment();

		try {
			FunctionNode.declareArguments(interpreter, expression.arguments, passedArguments);

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