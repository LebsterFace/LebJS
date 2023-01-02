package xyz.lebster.core.value;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.function.Executable;
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
	public static IteratorRecord getIterator(Interpreter interpreter, Expression expression) throws AbruptCompletion {
		final ObjectValue objectValue = expression.execute(interpreter).toObjectValue(interpreter);
		final String representation = ANSI.stripFormatting(expression.toRepresentationString());
		return getObjectIterator(interpreter, objectValue, representation);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-getiterator")
	@NonCompliant
	public static IteratorRecord getIterator(Interpreter interpreter, Value<?> obj) throws AbruptCompletion {
		final ObjectValue objectValue = obj.toObjectValue(interpreter);
		final String display = ANSI.stripFormatting(objectValue.toDisplayString());
		return getObjectIterator(interpreter, objectValue, display);
	}

	private static IteratorRecord getObjectIterator(Interpreter interpreter, ObjectValue objectValue, String display) throws AbruptCompletion {
		final PropertyDescriptor iteratorProperty = objectValue.getProperty(SymbolValue.iterator);
		if (iteratorProperty == null)
			throw error(new TypeError(interpreter, display + " is not iterable (does not contain a `Symbol.iterator` property)"));

		if (!(iteratorProperty.get(interpreter, objectValue) instanceof final Executable iteratorMethod))
			throw error(new TypeError(interpreter, display + "[Symbol.iterator] is not a function"));

		if (!(iteratorMethod.call(interpreter, objectValue) instanceof final ObjectValue iterator))
			throw error(new TypeError(interpreter, display + "[Symbol.iterator]() returned a non-object value"));

		final PropertyDescriptor nextProperty = iterator.getProperty(Names.next);
		if (nextProperty == null)
			throw error(new TypeError(interpreter, display + "[Symbol.iterator]() returned an object which does not contain a `next` property"));

		if (!(nextProperty.get(interpreter, iterator) instanceof final Executable executable))
			throw error(new TypeError(interpreter, display + "[Symbol.iterator]().next is not a function"));

		return new IteratorRecord(iterator, executable, display, "[Symbol.iterator]");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-iteratorcomplete")
	public static boolean iteratorComplete(Interpreter interpreter, ObjectValue iterResult) throws AbruptCompletion {
		// 1. Return ToBoolean(? Get(iterResult, "done")).
		return iterResult.get(interpreter, Names.done).isTruthy(interpreter);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-iteratorvalue")
	public static Value<?> iteratorValue(Interpreter interpreter, ObjectValue iterResult) throws AbruptCompletion {
		// 1. Return ? Get(iterResult, "value").
		return iterResult.get(interpreter, Names.value);
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-iterator-records")
	public static final class IteratorRecord {
		private final ObjectValue iteratorObject;
		private final Executable nextMethod;
		private final String errorString;
		private final String methodName;

		public IteratorRecord(ObjectValue iteratorObject, Executable nextMethod, String display, String methodName) {
			this.iteratorObject = iteratorObject;
			this.nextMethod = nextMethod;
			this.errorString = display;
			this.methodName = methodName;
		}

		@SpecificationURL("https://tc39.es/ecma262/multipage#sec-iteratornext")
		public ObjectValue next(Interpreter interpreter, Value<?> value) throws AbruptCompletion {
			// 1. If value is not present, then
			final var result = value == null ?
				// a. Let result be ? Call(iteratorRecord.[[NextMethod]], iteratorRecord.[[Iterator]]).
				nextMethod.call(interpreter, iteratorObject) :
				// 2. Else, a. Let result be ? Call(iteratorRecord.[[NextMethod]], iteratorRecord.[[Iterator]], « value »).
				nextMethod.call(interpreter, iteratorObject, value);

			// 3. If result is not an Object, throw a TypeError exception.
			if (!(result instanceof final ObjectValue next)) {
				final String representation = ANSI.stripFormatting(result.toDisplayString());
				throw error(new TypeError(interpreter,
					"%s%s().next() returned a non-object value (%s)".formatted(errorString, methodName, representation)));
			}

			// 4. Return result.
			return next;
		}

		@SpecificationURL("https://tc39.es/ecma262/multipage#sec-iteratorstep")
		public ObjectValue step(Interpreter interpreter) throws AbruptCompletion {
			// 1. Let result be ? IteratorNext(iteratorRecord).
			final var result = next(interpreter, null);
			// 2. Let done be ? IteratorComplete(result).
			final var done = iteratorComplete(interpreter, result);
			// 3. If done is true, return false.
			if (done) return null;
			// 4. Return result.
			return result;
		}

		public void collect(Interpreter interpreter, List<Value<?>> result) throws AbruptCompletion {
			ObjectValue iterResult = this.next(interpreter, null);
			while (!iteratorComplete(interpreter, iterResult)) {
				result.add(iteratorValue(interpreter, iterResult));
				iterResult = this.next(interpreter, null);
			}
		}
	}
}
