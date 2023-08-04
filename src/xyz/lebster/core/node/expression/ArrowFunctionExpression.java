package xyz.lebster.core.node.expression;

import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.FunctionParameters;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.value.function.ArrowFunction;

public record ArrowFunctionExpression(SourceRange range, FunctionParameters parameters, BlockStatement body, Expression implicitReturn, boolean hasFullBody) implements Expression {
	public ArrowFunctionExpression(SourceRange range, BlockStatement body, FunctionParameters parameters) {
		this(range, parameters, body, null, true);
	}

	public ArrowFunctionExpression(SourceRange range, Expression implicitReturn, FunctionParameters parameters) {
		this(range, parameters, null, implicitReturn, false);
	}

	@Override
	public ArrowFunction execute(Interpreter interpreter) {
		return new ArrowFunction(interpreter.intrinsics, this, interpreter.executionContext());
	}
}