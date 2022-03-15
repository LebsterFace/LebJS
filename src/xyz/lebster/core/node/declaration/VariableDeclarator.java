package xyz.lebster.core.node.declaration;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.executable.Executable;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.primitive.Undefined;

public record VariableDeclarator(String identifier, Expression init) implements ASTNode {
	@Override
	public void dump(int indent) {
		Dumper.dumpParameterized(indent, "VariableDeclarator", identifier);
		if (init == null) {
			Dumper.dumpSingle(indent + 1, "No Init");
		} else {
			init.dump(indent + 1);
		}
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> value = init == null ? Undefined.instance : init.execute(interpreter);
		final StringValue name = new StringValue(identifier);

		if (Executable.isAnonymousFunctionExpression(init)) {
			if (value instanceof final Executable function) {
				function.set(interpreter, Names.name, name);
				function.updateName(name.toFunctionName());
			}
		}

		interpreter.declareVariable(name, value);
		return Undefined.instance;
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