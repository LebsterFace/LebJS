package xyz.lebster.core.node.declaration;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.node.expression.Identifier;
import xyz.lebster.core.node.value.UndefinedValue;
import xyz.lebster.core.node.value.Value;

public record VariableDeclarator(Identifier identifier, Expression init) implements ASTNode {
	@Override
	public void dump(int indent) {
		Dumper.dumpParameterized(indent, "VariableDeclarator", identifier.toString());
		init.dump(indent + 1);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		interpreter.declareVariable(identifier, init.execute(interpreter));
		return UndefinedValue.instance;
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("let ");
		representation.append(identifier.value());
		representation.append(" = ");
		init.represent(representation);
		representation.append(';');
	}
}