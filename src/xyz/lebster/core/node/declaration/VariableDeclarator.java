package xyz.lebster.core.node.declaration;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.primitive.UndefinedValue;

public record VariableDeclarator(String identifier, Expression init) implements ASTNode {
	@Override
	public void dump(int indent) {
		Dumper.dumpParameterized(indent, "VariableDeclarator", identifier);
		init.dump(indent + 1);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> value = init == null ? UndefinedValue.instance : init.execute(interpreter);
		interpreter.declareVariable(identifier, value);
		return UndefinedValue.instance;
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("let ");
		representation.append(identifier);
		representation.append(" = ");
		init.represent(representation);
		representation.append(';');
	}
}