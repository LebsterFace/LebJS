package xyz.lebster.core.node;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.Undefined;
import xyz.lebster.core.node.value.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record Program(List<Statement> children) implements ASTNode {
	public Program() {
		this(new ArrayList<>());
	}

	public Program append(Statement node) {
		this.children.add(node);
		return this;
	}

	public Program append(Statement... nodes) {
		this.children.addAll(Arrays.asList(nodes));
		return this;
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		Value<?> lastValue = Undefined.instance;
		for (ASTNode child : children) lastValue = child.execute(interpreter);
		return lastValue;
	}

	@Override
	public void represent(StringRepresentation representation) {
		for (final ASTNode child : children) child.represent(representation);
	}

	@Override
	public void dump(int indent) {
		for (final ASTNode child : children) child.dump(indent);
	}
}