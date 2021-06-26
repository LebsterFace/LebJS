package xyz.lebster.core.node;

import xyz.lebster.core.expression.Expression;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.BooleanLiteral;
import xyz.lebster.core.value.Undefined;
import xyz.lebster.core.value.Value;
import xyz.lebster.exception.LanguageException;

public class IfStatement extends ScopeNode {
	public final Expression condition;
	public final ElseStatement elseStatement;
	public final boolean hasElseStatement;

	public IfStatement(Expression condition, ElseStatement elseStatement) {
		this.condition = condition;
		this.elseStatement = elseStatement;
		this.hasElseStatement = elseStatement != null;
	}

	public IfStatement(Expression condition) {
		this(condition, null);
	}

	@Override
	public void dump(int indent) {
		Interpreter.dumpName(indent, "IfStatement");
		condition.dump(indent + 1);
		for (final ASTNode child : children) {
			child.dump(indent + 1);
		}
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws LanguageException {
		final BooleanLiteral res = condition.execute(interpreter).toBooleanLiteral();
		if (res.value) {
			return executeChildren(interpreter);
		} else if (hasElseStatement) {
			return elseStatement.executeChildren(interpreter);
		} else {
			return new Undefined();
		}
	}
}
