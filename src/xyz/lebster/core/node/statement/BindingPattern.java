package xyz.lebster.core.node.statement;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Reference;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.declaration.VariableDeclaration;
import xyz.lebster.core.node.expression.LeftHandSideExpression;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.primitive.StringValue;

public record BindingPattern(VariableDeclaration.Kind kind, String identifier) implements LeftHandSideExpression {
	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "BindingPattern");
		Dumper.dumpIndicator(indent, "Kind");
		Dumper.dumpEnum(indent + 1, kind);
		Dumper.dumpIndicator(indent, "Identifier");
		Dumper.dumpSingle(indent + 1, identifier);
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(kind.name().toLowerCase());
		representation.append(" ");
		representation.append(identifier);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		throw new ShouldNotHappen("Call of BindingPattern#execute");
	}

	@Override
	public Reference toReference(Interpreter interpreter) {
		return new Reference(interpreter.lexicalEnvironment().variables(), new StringValue(identifier));
	}
}
