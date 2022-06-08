package xyz.lebster.core.node.declaration;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.expression.LeftHandSideExpression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.string.StringValue;

import java.util.List;

public record IdentifierExpression(StringValue name) implements AssignmentTarget, LeftHandSideExpression {
	public IdentifierExpression(String name) {
		this(new StringValue(name));
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return this.toReference(interpreter).getValue(interpreter);
	}

	@Override
	public List<BindingPair> getBindings(Interpreter interpreter, Value<?> input) {
		return List.of(new BindingPair(name, input));
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpValue(indent, "Identifier", name.value);
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-getidentifierreference")
	public Reference toReference(Interpreter interpreter) {
		return interpreter.getBinding(name);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(name.value);
	}

	public Value<?> assign(Interpreter interpreter, Value<?> value) throws AbruptCompletion {
		final Reference ref = this.toReference(interpreter);
		ref.putValue(interpreter, value);
		return value;
	}
}