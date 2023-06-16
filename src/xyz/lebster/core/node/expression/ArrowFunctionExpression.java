package xyz.lebster.core.node.expression;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.FunctionParameters;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.value.function.ArrowFunction;

public final class ArrowFunctionExpression implements Expression {
	public final FunctionParameters parameters;
	public final BlockStatement body;
	public final Expression implicitReturn;
	public final boolean hasFullBody;

	public ArrowFunctionExpression(FunctionParameters parameters, BlockStatement body, Expression implicitReturn, boolean hasFullBody) {
		this.parameters = parameters;
		this.body = body;
		this.implicitReturn = implicitReturn;
		this.hasFullBody = hasFullBody;
	}

	public ArrowFunctionExpression(BlockStatement body, FunctionParameters parameters) {
		this(parameters, body, null, true);
	}

	public ArrowFunctionExpression(Expression implicitReturn, FunctionParameters parameters) {
		this(parameters, null, implicitReturn, false);
	}

	@Override
	public ArrowFunction execute(Interpreter interpreter) {
		return new ArrowFunction(interpreter.intrinsics, this, interpreter.executionContext());
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.children("Parameters", parameters)
			.optionalChild("Body", body)
			.hiddenChild("Implicit Return", implicitReturn);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append('(');
		parameters.represent(representation);
		representation.append(')');
		representation.append(" => ");

		if (this.hasFullBody) {
			assert body != null;
			body.represent(representation);
		} else {
			assert implicitReturn != null;
			implicitReturn.represent(representation);
		}
	}
}