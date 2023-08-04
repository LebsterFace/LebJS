package xyz.lebster.core.node.declaration;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

public record VariableDeclarator(SourceRange range, AssignmentTarget target, Expression init) implements ASTNode {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		target.declare(interpreter, Kind.Let, init);
		return Undefined.instance;
	}
}