package xyz.lebster.core.node.statement;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.declaration.VariableDeclaration;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.node.expression.LeftHandSideExpression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.string.StringValue;

public record BindingPattern(VariableDeclaration.Kind kind, String identifier) implements LeftHandSideExpression {
	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent).self(this).enum_("Kind", kind).singleChild("Identifier", identifier);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(kind.name().toLowerCase());
		representation.append(" ");
		representation.append(identifier);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) {
		throw new ShouldNotHappen("Call of BindingPattern#execute");
	}

	@Override
	public Reference toReference(Interpreter interpreter) {
		return interpreter.lexicalEnvironment().getBinding(interpreter, new StringValue(identifier));
	}

	@Override
	public Value<?> assign(Interpreter interpreter, Expression rhs) throws AbruptCompletion {
		final Reference ref = this.toReference(interpreter);
		final Value<?> value = rhs.execute(interpreter);
		ref.putValue(interpreter, value);
		return value;
	}
}
