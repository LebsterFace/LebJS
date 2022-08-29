package xyz.lebster.core.node.expression;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.node.FunctionParameters;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.value.function.ArrowFunction;

public final class ArrowFunctionExpression implements Expression {
	public final FunctionParameters parameters;
	public final BlockStatement body;
	public final Expression implicitReturnExpression;
	public final boolean hasFullBody;

	public ArrowFunctionExpression(FunctionParameters parameters, BlockStatement body, Expression implicitReturnExpression, boolean hasFullBody) {
		this.parameters = parameters;
		this.body = body;
		this.implicitReturnExpression = implicitReturnExpression;
		this.hasFullBody = hasFullBody;
	}

	public ArrowFunctionExpression(BlockStatement body, FunctionParameters parameters) {
		this(parameters, body, null, true);
	}

	public ArrowFunctionExpression(Expression implicitReturnExpression, FunctionParameters parameters) {
		this(parameters, null, implicitReturnExpression, false);
	}

	@Override
	public ArrowFunction execute(Interpreter interpreter) {
		return new ArrowFunction(interpreter.intrinsics, this, interpreter.executionContext());
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.child("Parameters", parameters);

		if (this.hasFullBody) {
			assert body != null;
			for (final ASTNode child : body.children()) {
				child.dump(indent + 1);
			}
		} else {
			assert this.implicitReturnExpression != null;
			Dumper.dumpName(indent + 1, "ImplicitReturnStatement");
			this.implicitReturnExpression.dump(indent + 2);
		}
	}

	@Override
	public void represent(StringRepresentation representation) {
		parameters.represent(representation);
		representation.append(" => ");

		if (this.hasFullBody) {
			assert body != null;
			body.represent(representation);
		} else {
			assert implicitReturnExpression != null;
			implicitReturnExpression.represent(representation);
		}
	}
}