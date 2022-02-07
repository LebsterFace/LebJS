package xyz.lebster.core.node.statement;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.ASTNode;
import xyz.lebster.core.node.AppendableNode;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.primitive.Undefined;

import java.util.ArrayList;
import java.util.List;

public record BlockStatement(List<Statement> children) implements Statement, AppendableNode {
	public BlockStatement() {
		this(new ArrayList<>());
	}

	public void append(Statement node) {
		this.children.add(node);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final ExecutionContext context = interpreter.pushNewLexicalEnvironment();
		try {
			Value<?> lastValue = Undefined.instance;
			for (ASTNode child : children)
				lastValue = child.execute(interpreter);
			return lastValue;
		} finally {
			interpreter.exitExecutionContext(context);
		}
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.indent();
		representation.appendLine("{");

		for (ASTNode child : children) {
			representation.appendIndent();
			child.represent(representation);
			representation.appendLine();
		}

		representation.unindent();
		representation.appendIndent();
		representation.append('}');
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "BlockStatement");
		for (final ASTNode child : children) {
			child.dump(indent + 1);
		}
	}
}