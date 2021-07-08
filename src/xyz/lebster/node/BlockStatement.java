package xyz.lebster.node;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.interpreter.ScopeFrame;
import xyz.lebster.node.value.Undefined;
import xyz.lebster.node.value.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public record BlockStatement(List<ASTNode> children) implements Statement {
	public BlockStatement() {
		this(new ArrayList<>());
	}

	public BlockStatement append(ASTNode node) {
		this.children.add(node);
		return this;
	}

	public BlockStatement append(ASTNode... nodes) {
		this.children.addAll(Arrays.asList(nodes));
		return this;
	}

	public BlockStatement append(Collection<ASTNode> nodes) {
		this.children.addAll(nodes);
		return this;
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final ScopeFrame scopeFrame = interpreter.enterScope(this);
		Value<?> lastValue = new Undefined();

		try {
			for (ASTNode child : children) lastValue = child.execute(interpreter);
			return lastValue;
		} catch (AbruptCompletion completion) {
			throw completion;
		} finally {
			interpreter.exitScope(scopeFrame);
		}
	}

	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "BlockStatement");
		for (final ASTNode child : children) {
			child.dump(indent + 1);
		}
	}
}