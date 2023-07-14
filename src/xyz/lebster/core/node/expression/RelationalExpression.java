package xyz.lebster.core.node.expression;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.Value.isLessThan;

public record RelationalExpression(Expression left, Expression right, RelationalOp op) implements Expression {
	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-relational-operators")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> left_value = left.execute(interpreter);
		final Value<?> right_value = right.execute(interpreter);

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
					left_value.display(representation);
					representation.append("` in ");
					right_value.display(representation);
					throw error(new TypeError(interpreter, representation.toString()));
				}

				// 6. Return ? HasProperty(rval, ? ToPropertyKey(lval)).
				yield BooleanValue.of(object.hasProperty(left_value.toPropertyKey(interpreter)));
			}

			case InstanceOf -> instanceofOperator(interpreter, left_value, right_value);
		};
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-instanceofoperator")
	private BooleanValue instanceofOperator(Interpreter interpreter, Value<?> V, Value<?> target_) throws AbruptCompletion {
		// 1. If Type(target) is not Object, throw a TypeError exception.
		if (!(target_ instanceof final ObjectValue target))
			throw error(new TypeError(interpreter, "Right-hand side of `instanceof` is not an object"));
		// 2. Let instOfHandler be ? GetMethod(target, @@hasInstance).
		final Executable instOfHandler = target.getMethod(interpreter, SymbolValue.hasInstance);
		// 3. If instOfHandler is not undefined, then
		if (instOfHandler != null)
			// a. Return ! ToBoolean(? Call(instOfHandler, target, « V »)).
			return instOfHandler.call(interpreter, target, V).toBooleanValue(interpreter);
		// 4. If IsCallable(target) is false, throw a TypeError exception.
		final Executable targetFn = Executable.getExecutable(interpreter, target);
		// 5. Return ? OrdinaryHasInstance(target, V).
		return Executable.ordinaryHasInstance(interpreter, targetFn, V);
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