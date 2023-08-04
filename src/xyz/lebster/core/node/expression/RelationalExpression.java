package xyz.lebster.core.node.expression;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.Value.isLessThan;

public record RelationalExpression(SourceRange range, Expression left, Expression right, RelationalOp op) implements Expression {
	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-relational-operators")
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> x = left.execute(interpreter);
		final Value<?> y = right.execute(interpreter);

		return switch (op) {
			case LessThan -> {
				// 5. Let r be ? IsLessThan(x, y, true).
				final BooleanValue r = isLessThan(interpreter, x, y, true);
				// 6. If r is undefined, return false. Otherwise, return r.
				yield r == null ? BooleanValue.FALSE : r;
			}

			case GreaterThan -> {
				// 5. Let r be ? IsLessThan(y, x, false).
				final BooleanValue r = isLessThan(interpreter, y, x, false);
				// 6. If r is undefined, return false. Otherwise, return r.
				yield r == null ? BooleanValue.FALSE : r;
			}

			case LessThanEquals -> {
				// 5. Let r be ? IsLessThan(y, x, false).
				final BooleanValue r = isLessThan(interpreter, y, x, false);
				// 6. If r is true or undefined, return false. Otherwise, return true.
				//    (If r is false, return true. Otherwise, return false.)
				yield BooleanValue.of(r == BooleanValue.FALSE);
			}

			case GreaterThanEquals -> {
				// 5. Let r be ? IsLessThan(x, y, true).
				final BooleanValue r = isLessThan(interpreter, x, y, true);
				// 6. If r is true or undefined, return false. Otherwise, return true.
				//    (If r is false, return true. Otherwise, return false.)
				yield BooleanValue.of(r == BooleanValue.FALSE);
			}

			case In -> {
				// 5. If Type(y) is not Object, throw a TypeError exception.
				if (!(y instanceof final ObjectValue object)) {
					final StringBuilder builder = new StringBuilder();
					builder.append("Cannot use `in` operator to search for `");
					x.display(builder);
					builder.append("` in ");
					y.display(builder);
					throw error(new TypeError(interpreter, builder.toString()));
				}

				// 6. Return ? HasProperty(y, ? ToPropertyKey(x)).
				yield BooleanValue.of(object.hasProperty(x.toPropertyKey(interpreter)));
			}

			case InstanceOf -> instanceofOperator(interpreter, x, y);
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

	public enum RelationalOp {
		LessThan, GreaterThan,
		LessThanEquals, GreaterThanEquals,
		InstanceOf, In
	}
}