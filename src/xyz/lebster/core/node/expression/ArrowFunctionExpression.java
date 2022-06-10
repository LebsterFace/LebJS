package xyz.lebster.core.node.expression;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.node.FunctionArguments;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.value.function.ArrowFunction;

public final class ArrowFunctionExpression implements Expression {
	public final FunctionArguments arguments;
	public final BlockStatement body;
	public final Expression implicitReturnExpression;
	public final boolean hasFullBody;

	public ArrowFunctionExpression(FunctionArguments arguments, BlockStatement body, Expression implicitReturnExpression, boolean hasFullBody) {
		this.arguments = arguments;
		this.body = body;
		this.implicitReturnExpression = implicitReturnExpression;
		this.hasFullBody = hasFullBody;
	}

	public ArrowFunctionExpression(BlockStatement body, FunctionArguments arguments) {
		this(arguments, body, null, true);
	}

	public ArrowFunctionExpression(Expression implicitReturnExpression, FunctionArguments arguments) {
		this(arguments, null, implicitReturnExpression, false);
	}

	@Override
	public ArrowFunction execute(Interpreter interpreter) {
		return new ArrowFunction(interpreter, this, interpreter.executionContext());
	}

	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.child("Arguments", arguments);

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
		arguments.represent(representation);
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