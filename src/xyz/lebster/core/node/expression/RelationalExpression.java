package xyz.lebster.core.node.expression;

import xyz.lebster.core.Dumper;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.*;
import xyz.lebster.core.node.value.object.Executable;
import xyz.lebster.core.node.value.object.ObjectValue;
import xyz.lebster.core.runtime.error.TypeError;

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
		final Value<?> left_value = left.execute(interpreter);
		final Value<?> right_value = right.execute(interpreter);

		// https://tc39.es/ecma262/multipage#sec-relational-operators-runtime-semantics-evaluation
		return switch (op) {
			case LessThan -> {
				// 5. Let r be ? IsLessThan(left_value, right_value, true).
				final BooleanValue r = isLessThan(interpreter, left_value, right_value, true);
				// 6. If r is undefined, return false. Otherwise, return r.
				yield r == null ? BooleanValue.FALSE : r;
			}

			case GreaterThan -> {
				// 5. Let r be ? IsLessThan(rval, lval, false).
				final BooleanValue r = isLessThan(interpreter, right_value, left_value, false);
				// 6. If r is undefined, return false. Otherwise, return r.
				yield r == null ? BooleanValue.FALSE : r;
			}

			case LessThanEquals -> {
				// 5. Let r be ? IsLessThan(rval, lval, false).
				final BooleanValue r = isLessThan(interpreter, right_value, left_value, false);
				// 6. If r is true or undefined, return false. Otherwise, return true.
				//    (If r is false, return true. Otherwise, return false.)
				yield BooleanValue.of(r == BooleanValue.FALSE);
			}

			case GreaterThanEquals -> {
				// 5. Let r be ? IsLessThan(lval, rval, true).
				final BooleanValue r = isLessThan(interpreter, left_value, right_value, true);
				// 6. If r is true or undefined, return false. Otherwise, return true.
				//    (If r is false, return true. Otherwise, return false.)
				yield BooleanValue.of(r == BooleanValue.FALSE);
			}

			case In -> {
				// 5. If Type(rval) is not Object, throw a TypeError exception.
				if (!(right_value instanceof final ObjectValue object)) {
					final var representation = new StringRepresentation();
					representation.append("Cannot use `in` operator to search for `");
					left_value.represent(representation);
					representation.append("` in ");
					right_value.represent(representation);
					throw AbruptCompletion.error(new TypeError(representation.toString()));
				}

				// 6. Return ? HasProperty(rval, ? ToPropertyKey(lval)).
				yield BooleanValue.of(object.hasProperty(left_value.toPropertyKey(interpreter)));
			}

			case InstanceOf -> instanceofOperator(interpreter, left_value, right_value);
		};
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-instanceofoperator")
	private BooleanValue instanceofOperator(Interpreter interpreter, Value<?> V, Value<?> target) throws AbruptCompletion {
		// 1. If Type(target) is not Object, throw a TypeError exception.
		if (target.type != Value.Type.Object)
			throw AbruptCompletion.error(new TypeError("Right-hand side of `instanceof` is not an object"));
		// 2. Let instOfHandler be ? GetMethod(target, @@hasInstance).
		final Value<?> instOfHandler = target.getMethod(interpreter, SymbolValue.hasInstance);
		// 3. If instOfHandler is not undefined, then
		if (instOfHandler instanceof final Executable<?> executable)
			// a. Return ! ToBoolean(? Call(instOfHandler, target, « V »)).
			return executable.call(interpreter, target, V).toBooleanValue(interpreter);
		// 4. If IsCallable(target) is false, throw a TypeError exception.
		if (!(target instanceof final Executable<?> executable))
			throw AbruptCompletion.error(new TypeError("Not a function!"));
		// 5. Return ? OrdinaryHasInstance(target, V).
		return executable.ordinaryHasInstance(V);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-isstringprefix")
	private boolean isStringPrefix(String p, String q) {
		// 1. If ! StringIndexOf(q, p, 0) is 0, return true.
		// 2. Else, return false.
		return q.indexOf(p) == 0;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-islessthan")
	private BooleanValue isLessThan(Interpreter interpreter, Value<?> x, Value<?> y, boolean leftFirst) throws AbruptCompletion {
		// 1. If the LeftFirst flag is true, then
		Value<?> px = null;
		Value<?> py = null;

		if (leftFirst) {
			// a. Let px be ? ToPrimitive(x, number).
			px = x.toPrimitive(interpreter, Value.Type.Number);
			// b. Let py be ? ToPrimitive(y, number).
			py = y.toPrimitive(interpreter, Value.Type.Number);
		}
		// 2. Else,
		else {
			// a. NOTE: The order of evaluation needs to be reversed to preserve left to right evaluation.
			// b. Let py be ? ToPrimitive(y, number).
			py = y.toPrimitive(interpreter, Value.Type.Number);
			// c. Let px be ? ToPrimitive(x, number).
			px = x.toPrimitive(interpreter, Value.Type.Number);
		}


		// 3. If Type(px) is String and Type(py) is String, then
		if (px instanceof final StringValue string_px && py instanceof final StringValue string_py) {

			// a. If IsStringPrefix(py, px) is true, return false.
			if (isStringPrefix(string_py.value, string_px.value)) return BooleanValue.FALSE;
			// b. If IsStringPrefix(px, py) is true, return true.
			if (isStringPrefix(string_px.value, string_py.value)) return BooleanValue.TRUE;

			// c. Let k be the smallest non-negative integer such that the code unit at index k
			//    within px is different from the code unit at index k within py.
			//    (There must be such a k, for neither String is a prefix of the other.)
			int k = 0;
			while (k < string_px.value.length()) {
				if (string_px.value.charAt(k) != string_py.value.charAt(k)) {
					break;
				}

				k++;
			}

			// d. Let m be the integer that is the numeric value of the code unit at index k within px.
			int m = string_px.value.charAt(k);
			// e. Let n be the integer that is the numeric value of the code unit at index k within py.
			int n = string_py.value.charAt(k);
			// f. If m < n, return true. Otherwise, return false.
			return BooleanValue.of(m < n);
		}
		// 4. Else,
		else {
			// FIXME: BigInt for this entire block

			// c. NOTE: Because px and py are primitive values, evaluation order is not important.
			// d. Let nx be ? ToNumeric(px).
			final NumberValue nx = px.toPrimitive(interpreter, Value.Type.Number).toNumberValue(interpreter);
			// e. Let ny be ? ToNumeric(py).
			final NumberValue ny = py.toPrimitive(interpreter, Value.Type.Number).toNumberValue(interpreter);

			// 1. Return Number::lessThan(nx, ny).
			return nx.lessThan(ny);
		}
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