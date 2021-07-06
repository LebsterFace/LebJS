package xyz.lebster.node;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.node.expression.Expression;
import xyz.lebster.node.expression.Identifier;
import xyz.lebster.node.value.Value;

public record VariableDeclarator(Identifier identifier, Expression init) implements ASTNode {
	@Override
	public void dump(int indent) {
		Dumper.dumpParameterized(indent, "VariableDeclarator", identifier.toString());
		init.dump(indent + 1);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return interpreter.declareVariable(identifier, init.execute(interpreter));
	}
}