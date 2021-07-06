package xyz.lebster.node.expression;

import xyz.lebster.Dumper;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.interpreter.Reference;
import xyz.lebster.node.SpecificationURL;
import xyz.lebster.node.value.Value;
import xyz.lebster.runtime.LanguageError;

public record AssignmentExpression(Expression left, Expression right, AssignmentOp op) implements Expression {
	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "AssignmentExpression");
		Dumper.dumpIndicated(indent + 1, "Left", left);
		Dumper.dumpEnum(indent + 1, "Operator", op);
		Dumper.dumpIndicated(indent + 1, "Right", right);
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-assignment-operators-runtime-semantics-evaluation")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		if (!(left instanceof final LeftHandSideExpression lhs)) {
			throw new AbruptCompletion(new LanguageError("Invalid left-hand side in expression"), AbruptCompletion.Type.Throw);
		}

		final Reference lref = lhs.toReference(interpreter);
		final Value<?> rval = right.execute(interpreter);

		switch (op) {
			case Assign -> lref.setValue(interpreter, rval);
		}

		return rval;
	}

	public enum AssignmentOp {
		Assign
	}
}