package xyz.lebster.core.node.statement;

import xyz.lebster.core.DumpBuilder;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.globals.Undefined;

public record IfStatement(Expression condition, Statement consequence, Statement elseStatement) implements Statement {
	@Override
	public void dump(int indent) {
		DumpBuilder.begin(indent)
			.self(this)
			.child("Condition", condition)
			.child("Consequence", consequence)
			.hiddenChild("Else", elseStatement);
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		if (condition.execute(interpreter).isTruthy(interpreter)) {
			return consequence.execute(interpreter);
		} else if (elseStatement == null) {
			return Undefined.instance;
		} else {
			return elseStatement.execute(interpreter);
		}
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append("if (");
		condition.represent(representation);
		representation.append(") ");
		consequence.represent(representation);
		if (elseStatement != null) {
			representation.append(" else ");
			elseStatement.represent(representation);
		}
	}
}