package xyz.lebster.core.value;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.object.PropertyDescriptor;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

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
		return getObjectIterator(interpreter, objectValue, obj);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-getiterator")
	@NonCompliant
	public static ObjectIterator getIterator(Interpreter interpreter, Value<?> value) throws AbruptCompletion {
		final ObjectValue objectValue = value.toObjectValue(interpreter);
		final String obj = ANSI.stripFormatting(value.toDisplayString());
		return getObjectIterator(interpreter, objectValue, obj);
	}

	private static ObjectIterator getObjectIterator(Interpreter interpreter, ObjectValue objectValue, String obj) throws AbruptCompletion {
		final PropertyDescriptor iteratorProperty = objectValue.getProperty(SymbolValue.iterator);
		if (iteratorProperty == null)
			throw error(new TypeError(interpreter, obj + " is not iterable (does not contain a `Symbol.iterator` property)"));

		if (!(iteratorProperty.get(interpreter, objectValue) instanceof final Executable iteratorMethod))
			throw error(new TypeError(interpreter, obj + "[Symbol.iterator] is not a function"));

		if (!(iteratorMethod.call(interpreter, objectValue) instanceof final ObjectValue iterator))
			throw error(new TypeError(interpreter, obj + "[Symbol.iterator]() returned a non-object value"));

		final PropertyDescriptor nextProperty = iterator.getProperty(Names.next);
		if (nextProperty == null)
			throw error(new TypeError(interpreter, obj + "[Symbol.iterator]() returned an object which does not contain a `next` property"));

		if (!(nextProperty.get(interpreter, iterator) instanceof final Executable executable))
			throw error(new TypeError(interpreter, obj + "[Symbol.iterator]().next is not a function"));

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
				throw error(new TypeError(interpreter, this.errorString + "[Symbol.iterator]().next() returned a non-object value (" + representation + ")"));
			}

			final boolean done = next.get(interpreter, Names.done).isTruthy(interpreter);
			final Value<?> value = done ? Undefined.instance : next.get(interpreter, Names.value);
			return new IteratorResult(value, done);
		}

		public void collect(List<Value<?>> result) throws AbruptCompletion {
			IteratorResult next = this.next();
			while (!next.done()) {
				result.add(next.value());
				next = this.next();
			}
		}
	}
}
