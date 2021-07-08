package xyz.lebster.node.expression;

import xyz.lebster.Dumper;
import xyz.lebster.exception.NotImplemented;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.node.SpecificationURL;
import xyz.lebster.node.value.BooleanLiteral;
import xyz.lebster.node.value.Dictionary;
import xyz.lebster.node.value.Value;
import xyz.lebster.runtime.TypeError;

public record RelationalExpression(Expression left, Expression right, RelationalOp op) implements Expression {
	@Override
	public void dump(int indent) {
		Dumper.dumpName(indent, "RelationalExpression");
		Dumper.dumpIndicated(indent + 1, "Left", left);
		Dumper.dumpEnum(indent + 1, "Operator", op);
		Dumper.dumpIndicated(indent + 1, "Right", right);
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-relational-operators")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> lval = left.execute(interpreter);
		final Value<?> rval = right.execute(interpreter);

//		FIXME: Comply with spec
		// https://tc39.es/ecma262/multipage#sec-relational-operators-runtime-semantics-evaluation
		return new BooleanLiteral(switch (op) {
			case LessThan -> lval.toNumericLiteral(interpreter).value < rval.toNumericLiteral(interpreter).value;
			case GreaterThan -> lval.toNumericLiteral(interpreter).value > rval.toNumericLiteral(interpreter).value;
			case LessThanEquals -> lval.toNumericLiteral(interpreter).value <= rval.toNumericLiteral(interpreter).value;
			case GreaterThanEquals -> lval.toNumericLiteral(interpreter).value >= rval.toNumericLiteral(interpreter).value;
			case In -> {
				if (rval instanceof final Dictionary dictionary) {
					yield  dictionary.hasOwnProperty(lval.toStringLiteral(interpreter));
				} else {
					throw new AbruptCompletion(new TypeError("Can only use 'in' operator on an object!"), AbruptCompletion.Type.Throw);
				}
			}

			case InstanceOf -> {
				throw new NotImplemented("`instanceof` operator");
			}
		});
	}

	public enum RelationalOp {
		LessThan, GreaterThan,
		LessThanEquals, GreaterThanEquals,
		InstanceOf, In
	}
}