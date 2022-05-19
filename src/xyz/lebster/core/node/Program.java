package xyz.lebster.core.node;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.statement.Statement;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

import java.util.ArrayList;
import java.util.List;

public record Program(List<Statement> children) implements AppendableNode {
	public Program() {
		this(new ArrayList<>());
	}

	public void append(Statement node) {
		this.children.add(node);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		Value<?> lastValue = Undefined.instance;
		for (final ASTNode child : children) {
			lastValue = child.execute(interpreter);
		}

		return lastValue;
	}

	@Override
	public void represent(StringRepresentation representation) {
		for (final ASTNode child : children)
			child.represent(representation);
	}

	@Override
	public void dump(int indent) {
		for (final ASTNode child : children)
			child.dump(indent);
	}
}