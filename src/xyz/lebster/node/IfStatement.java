package xyz.lebster.node;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.interpreter.StringRepresentation;
import xyz.lebster.node.expression.Expression;
import xyz.lebster.node.value.BooleanLiteral;
import xyz.lebster.node.value.Undefined;
import xyz.lebster.node.value.Value;

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
		if (condition.execute(interpreter).isTruthy()) {
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