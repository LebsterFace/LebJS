package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.SpecificationURL;
import xyz.lebster.core.node.value.BooleanLiteral;
import xyz.lebster.core.node.value.Dictionary;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.runtime.TypeError;

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
			case LessThan -> lval.toNumericLiteral().value < rval.toNumericLiteral().value;
			case GreaterThan -> lval.toNumericLiteral().value > rval.toNumericLiteral().value;
			case LessThanEquals -> lval.toNumericLiteral().value <= rval.toNumericLiteral().value;
			case GreaterThanEquals -> lval.toNumericLiteral().value >= rval.toNumericLiteral().value;
			case In -> {
				if (rval instanceof final Dictionary dictionary) {
					yield dictionary.hasOwnProperty(lval.toStringLiteral());
				} else {
					throw AbruptCompletion.error(new TypeError("Can only use 'in' operator on an object!"));
				}
			}

			case InstanceOf -> throw new NotImplemented("`instanceof` operator");
		});
	}

	@Override
	public void represent(StringRepresentation representation) {
		left.represent(representation);
		representation.append(' ');
		representation.append(switch (op) {
			case LessThan -> '<';
			case GreaterThan -> '>';
			case LessThanEquals -> "<=";
			case GreaterThanEquals -> ">=";
			case InstanceOf -> "instanceof";
			case In -> "in";
		});
		representation.append(' ');
		right.represent(representation);
	}

	public enum RelationalOp {
		LessThan, GreaterThan,
		LessThanEquals, GreaterThanEquals,
		InstanceOf, In
	}
}