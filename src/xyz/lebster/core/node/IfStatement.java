package xyz.lebster.core.node;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.node.value.Undefined;
import xyz.lebster.core.node.value.Value;

public record IfStatement(Expression condition, Statement consequence, Statement elseStatement) implements Statement {
	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "IfStatement");
		Dumper.dumpIndicated(indent + 1, "Condition", condition);
		Dumper.dumpIndicated(indent + 1, "Consequence", consequence);
		if (elseStatement != null) {
			Dumper.dumpIndicated(indent + 1, "ElseStatement", elseStatement);
		}
	}

	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		if (condition.execute(interpreter).isTruthy(interpreter)) {
			return consequence.execute(interpreter);
		} else if (elseStatement == null) {
			return new Undefined();
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