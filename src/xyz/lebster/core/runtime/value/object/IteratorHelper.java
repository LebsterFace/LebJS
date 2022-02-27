package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.executable.Executable;
import xyz.lebster.core.runtime.value.primitive.SymbolValue;
import xyz.lebster.core.runtime.value.primitive.Undefined;

import java.util.List;

public final class IteratorHelper {
	private IteratorHelper() {
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-getiterator")
	@NonCompliant
	public static ObjectIterator getIterator(Interpreter interpreter, Expression expression) throws AbruptCompletion {
		final ObjectValue objectValue = expression.execute(interpreter).toObjectValue(interpreter);

		if (!(objectValue.get(interpreter, SymbolValue.iterator) instanceof final Executable<?> iteratorMethod)) {
			final var representation = new StringRepresentation();
			expression.represent(representation);
			representation.append(" is not iterable");
			throw AbruptCompletion.error(new TypeError(representation.toString()));
		}

		if (!(iteratorMethod.call(interpreter, objectValue) instanceof final ObjectValue iterator))
			throw AbruptCompletion.error(new TypeError("Result of the Symbol.iterator method is not an object"));

		final Executable<?> nextMethod = Executable.getExecutable(iterator.get(interpreter, Names.next));
		return new ObjectIterator(interpreter, iterator, nextMethod);
	}

	public static final class ObjectIterator {
		private final Interpreter interpreter;
		private final ObjectValue iteratorObject;
		private final Executable<?> nextMethod;

		private ObjectIterator(Interpreter interpreter, ObjectValue iteratorObject, Executable<?> nextMethod) {
			this.interpreter = interpreter;
			this.iteratorObject = iteratorObject;
			this.nextMethod = nextMethod;
		}

		public IteratorResult next() throws AbruptCompletion {
			if (!(nextMethod.call(interpreter, iteratorObject) instanceof ObjectValue next))
				throw AbruptCompletion.error(new TypeError("Iterator result is not an object"));

			final boolean done = next.get(interpreter, Names.done).isTruthy(interpreter);
			final Value<?> value = done ? Undefined.instance : next.get(interpreter, Names.value);
			return new IteratorResult(value, done);
		}

		public void collect(List<Value<?>> result) throws AbruptCompletion {
			IteratorResult next = this.next();
			while (!next.done) {
				result.add(next.value);
				next = this.next();
			}
		}
	}

	public static final class IteratorResult {
		public final Value<?> value;
		public final boolean done;

		private IteratorResult(Value<?> value, boolean done) {
			this.value = value;
			this.done = done;
		}
	}
}
