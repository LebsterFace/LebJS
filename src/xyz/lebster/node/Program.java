package xyz.lebster.node;

import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.interpreter.StringRepresentation;
import xyz.lebster.node.value.Value;


public class Program implements ASTNode {
	public final BlockStatement body;

	public Program() {
		this.body = new BlockStatement();
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		return body.execute(interpreter);
	}

	@Override
	public void dump(int indent) {
		for (ASTNode child : body.children()) {
			child.dump(indent);
		}
	}

	@Override
	public void represent(StringRepresentation representation) {
		for (ASTNode child : body.children()) {
			child.represent(representation);
		}
	}
}