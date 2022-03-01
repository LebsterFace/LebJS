package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.TypeError;
import xyz.lebster.core.runtime.value.executable.Executable;
import xyz.lebster.core.runtime.value.object.property.PropertyDescriptor;
import xyz.lebster.core.runtime.value.primitive.SymbolValue;
import xyz.lebster.core.runtime.value.primitive.Undefined;

import java.util.List;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

public final class IteratorHelper {
	private IteratorHelper() {
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-getiterator")
	@NonCompliant
	public static ObjectIterator getIterator(Interpreter interpreter, Expression expression) throws AbruptCompletion {
		final ObjectValue objectValue = expression.execute(interpreter).toObjectValue(interpreter);
		final String obj = ANSI.stripFormatting(expression.toRepresentationString());

		final PropertyDescriptor iteratorProperty = objectValue.getProperty(SymbolValue.iterator);
		if (iteratorProperty == null)
			throw error(new TypeError(obj + " is not iterable (does not contain a `Symbol.iterator` property)"));

		if (!(iteratorProperty.get(interpreter, objectValue) instanceof final Executable iteratorMethod))
			throw error(new TypeError(obj + "[Symbol.iterator] is not a function"));

		if (!(iteratorMethod.call(interpreter, objectValue) instanceof final ObjectValue iterator))
			throw error(new TypeError(obj + "[Symbol.iterator]() returned a non-object value"));

		final PropertyDescriptor nextProperty = iterator.getProperty(Names.next);
		if (nextProperty == null)
			throw error(new TypeError(obj + "[Symbol.iterator]() returned an object which does not contain a `next` property"));

		if (!(nextProperty.get(interpreter, iterator) instanceof final Executable executable))
			throw error(new TypeError(obj + "[Symbol.iterator]().next is not a function"));

		return new ObjectIterator(interpreter, iterator, executable, obj);
	}

	public static final class ObjectIterator {
		private final Interpreter interpreter;
		private final ObjectValue iteratorObject;
		private final Executable nextMethod;
		private final String errorString;

		private ObjectIterator(Interpreter interpreter, ObjectValue iteratorObject, Executable nextMethod, String errorString) {
			this.interpreter = interpreter;
			this.iteratorObject = iteratorObject;
			this.nextMethod = nextMethod;
			this.errorString = errorString;
		}

		public IteratorResult next() throws AbruptCompletion {
			final Value<?> iteratorResult = nextMethod.call(interpreter, iteratorObject);
			if (!(iteratorResult instanceof ObjectValue next)) {
				final String representation = ANSI.stripFormatting(iteratorResult.toDisplayString());
				throw error(new TypeError(this.errorString + "[Symbol.iterator]().next() returned a non-object value (" + representation + ")"));
			}

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
