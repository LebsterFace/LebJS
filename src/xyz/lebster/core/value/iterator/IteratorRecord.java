package xyz.lebster.core.value.iterator;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.object.ObjectValue;

import java.util.List;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.iterator.IteratorPrototype.iteratorComplete;
import static xyz.lebster.core.value.iterator.IteratorPrototype.iteratorValue;

@NonCompliant
@SpecificationURL("https://tc39.es/ecma262/multipage#sec-iterator-records")
public record IteratorRecord(ObjectValue iteratorObject, Value<?> nextMethod) {
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-iteratornext")
	public ObjectValue next(Interpreter interpreter, Value<?> value) throws AbruptCompletion {
		final Executable executable = Executable.getExecutable(interpreter, nextMethod);

		// 1. If value is not present, then
		final Value<?> result = value == null ?
			// a. Let result be ? Call(iteratorRecord.[[NextMethod]], iteratorRecord.[[Iterator]]).
			executable.call(interpreter, iteratorObject) :
			// 2. Else, a. Let result be ? Call(iteratorRecord.[[NextMethod]], iteratorRecord.[[Iterator]], « value »).
			executable.call(interpreter, iteratorObject, value);

		// 3. If result is not an Object, throw a TypeError exception.
		if (!(result instanceof final ObjectValue next))
			throw error(new TypeError(interpreter, "Iterator result %s is not an object".formatted(result.toDisplayString(true))));

		// 4. Return result.
		return next;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-iteratorstep")
	public ObjectValue step(Interpreter interpreter) throws AbruptCompletion {
		// 1. Let result be ? IteratorNext(iteratorRecord).
		final ObjectValue result = this.next(interpreter, null);
		// 2. Let done be Completion(IteratorComplete(result)).
		boolean done = iteratorComplete(interpreter, result);
		// NOTE: Steps 3 & 4 do not need special-casing.
		// 5. If done is true, then
		if (done) {
			// a. Set iteratorRecord.[[Done]] to true.
			// b. Return DONE.
			return null;
		}

		// 6. Return result.
		return result;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-iteratorstepvalue")
	public Value<?> stepValue(Interpreter interpreter) throws AbruptCompletion {
		// 1. Let result be ? IteratorStep(iteratorRecord).
		final ObjectValue result = step(interpreter);
		// 2. If result is DONE, then
		if (result == null) {
			// a. Return DONE.
			return null;
		}

		// 3. Let value be Completion(IteratorValue(result)).
		// NOTE: Step 4 does not need special-casing.
		// 5. Return ? value.
		return iteratorValue(interpreter, result);
	}

	public void collect(Interpreter interpreter, List<Value<?>> result) throws AbruptCompletion {
		ObjectValue iterResult = this.next(interpreter, null);
		while (!iteratorComplete(interpreter, iterResult)) {
			result.add(iteratorValue(interpreter, iterResult));
			iterResult = this.next(interpreter, null);
		}
	}
}
