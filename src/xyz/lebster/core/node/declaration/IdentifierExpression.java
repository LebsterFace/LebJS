package xyz.lebster.core.node.declaration;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.expression.LeftHandSideExpression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.primitive.string.StringValue;

public record IdentifierExpression(SourceRange range, StringValue name) implements LeftHandSideExpression {
	public IdentifierExpression(SourceRange range, String name) {
		this(range, new StringValue(name));
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return this.toReference(interpreter).getValue(interpreter);
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-getidentifierreference")
	public Reference toReference(Interpreter interpreter) {
		return interpreter.getBinding(name);
	}

	@Override
	public void declare(Interpreter interpreter, Kind kind, Value<?> value) throws AbruptCompletion {
		interpreter.declareVariable(kind, name, value);
	}
}