package xyz.lebster.core.value.iterator;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.node.expression.Expression;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.error.range.RangeError;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.object.PropertyDescriptor;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.number.NumberValue;
import xyz.lebster.core.value.primitive.symbol.SymbolValue;

import java.util.ArrayList;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.function.NativeFunction.argument;
import static xyz.lebster.core.value.primitive.number.NumberPrototype.toIntegerOrInfinity;

@SpecificationURL("https://tc39.es/proposal-iterator-helpers#sec-iteratorprototype")
public final class IteratorPrototype extends ObjectValue {

	public IteratorPrototype(Intrinsics intrinsics) {
		super(intrinsics);
		// TODO: 3.1.3.1 Iterator.prototype.constructor

		putMethod(intrinsics, Names.map, 1, IteratorPrototype::map);
		putMethod(intrinsics, Names.filter, 1, IteratorPrototype::filter);
		putMethod(intrinsics, Names.take, 1, IteratorPrototype::take);
		putMethod(intrinsics, Names.drop, 1, IteratorPrototype::drop);
		putMethod(intrinsics, Names.flatMap, 1, IteratorPrototype::flatMap);
		putMethod(intrinsics, Names.reduce, 1, IteratorPrototype::reduce);
		putMethod(intrinsics, Names.toArray, 0, IteratorPrototype::toArray);
		putMethod(intrinsics, Names.forEach, 1, IteratorPrototype::forEach);
		putMethod(intrinsics, Names.some, 1, IteratorPrototype::some);
		putMethod(intrinsics, Names.every, 1, IteratorPrototype::every);
		putMethod(intrinsics, Names.find, 1, IteratorPrototype::find);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-getiterator")
	@NonCompliant
	public static IteratorRecord getIterator(Interpreter interpreter, Expression expression) throws AbruptCompletion {
		final ObjectValue objectValue = expression.execute(interpreter).toObjectValue(interpreter);
		final String sourceText = expression.range().getText();
		return getObjectIterator(interpreter, objectValue, sourceText);
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-getiterator")
	public static IteratorRecord getIterator(Interpreter interpreter, Value<?> obj) throws AbruptCompletion {
		final ObjectValue objectValue = obj.toObjectValue(interpreter);
		return getObjectIterator(interpreter, objectValue, objectValue.toDisplayString(true));
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

		return new IteratorRecord(iterator, executable);
	}

	@SpecificationURL("https://tc39.es/proposal-iterator-helpers#sec-getiteratordirect")
	private static IteratorRecord getIteratorDirect(Interpreter interpreter, ObjectValue obj) throws AbruptCompletion {
		// 2.2.1 GetIteratorDirect ( obj )

		// 1. Let nextMethod be ? Get(obj, "next").
		final Value<?> nextMethod = obj.get(interpreter, Names.next);
		// 2. Let iteratorRecord be Record { [[Iterator]]: obj, [[NextMethod]]: nextMethod, [[Done]]: false }.
		// 3. Return iteratorRecord.
		return new IteratorRecord(obj, nextMethod);
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

	private static ObjectValue requireIteratorLike(Interpreter interpreter, String methodName) throws AbruptCompletion {
		if (interpreter.thisValue() instanceof final ObjectValue obj) return obj;
		throw error(new TypeError(interpreter, "Iterator.prototype.%s requires that 'this' be an object.".formatted(methodName)));
	}

	@SpecificationURL("https://tc39.es/proposal-iterator-helpers#sec-iteratorprototype.map")
	private static IteratorObject map(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 3.1.3.2 Iterator.prototype.map ( mapper )
		final Value<?> mapper_ = argument(0, arguments);

		// 1. Let O be the `this` value.
		// 2. If O is not an Object, throw a TypeError exception.
		final ObjectValue O = requireIteratorLike(interpreter, "map");
		// 3. If IsCallable(mapper) is false, throw a TypeError exception.
		final Executable mapper = Executable.getExecutable(interpreter, mapper_);
		// 4. Let iterated be ? GetIteratorDirect(O).
		final IteratorRecord iterated = getIteratorDirect(interpreter, O);
		// TODO: 5. Let closure be a new Abstract Closure with no parameters that captures iterated and mapper and performs the following steps when called:
		// TODO: 6. Let result be CreateIteratorFromClosure(closure, "Iterator Helper", %IteratorHelperPrototype%, « [[UnderlyingIterator]] »).
		// TODO: 7. Set result.[[UnderlyingIterator]] to iterated.
		// 8. Return result.
		return new IteratorObject(interpreter.intrinsics) {
			// a. Let counter be 0.
			private int counter = 0;

			// b. Repeat,
			@Override
			public Value<?> next(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
				// i. Let next be ? IteratorStep(iterated).
				final ObjectValue next = iterated.step(interpreter);
				// ii. If next is false, return undefined.
				if (next == null) return setCompleted();

				// iii. Let value be ? IteratorValue(next).
				final Value<?> value = iteratorValue(interpreter, next);
				// iv. Let mapped be Completion(Call(mapper, undefined, « value, 𝔽(counter) »)).
				final Value<?> mapped = mapper.call(interpreter, Undefined.instance, value, new NumberValue(counter));
				// FIXME: v. IfAbruptCloseIterator(mapped, iterated).
				// FIXME: vi. Let completion be Completion(Yield(mapped)).
				// FIXME: vii. IfAbruptCloseIterator(completion, iterated).
				// viii. Set counter to counter + 1.
				counter = counter + 1;
				return mapped;
			}
		};
	}

	@SpecificationURL("https://tc39.es/proposal-iterator-helpers#sec-iteratorprototype.filter")
	private static IteratorObject filter(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 3.1.3.3 Iterator.prototype.filter ( predicate )
		final Value<?> predicate_ = argument(0, arguments);

		// 1. Let O be the `this` value.
		// 2. If O is not an Object, throw a TypeError exception.
		final ObjectValue O = requireIteratorLike(interpreter, "filter");
		// 3. If IsCallable(predicate) is false, throw a TypeError exception.
		final Executable predicate = Executable.getExecutable(interpreter, predicate_);
		// 4. Let iterated be ? GetIteratorDirect(O).
		final IteratorRecord iterated = getIteratorDirect(interpreter, O);
		// TODO: 5. Let closure be a new Abstract Closure with no parameters that captures iterated and predicate and performs the following steps when called:
		// TODO: 6. Let result be CreateIteratorFromClosure(closure, "Iterator Helper", %IteratorHelperPrototype%, « [[UnderlyingIterator]] »).
		// TODO: 7. Set result.[[UnderlyingIterator]] to iterated.
		// 8. Return result.
		return new IteratorObject(interpreter.intrinsics) {
			// a. Let counter be 0.
			private int counter = 0;

			@Override
			public Value<?> next(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
				// b. Repeat,
				while (true) {
					// i. Let next be ? IteratorStep(iterated).
					final ObjectValue next = iterated.step(interpreter);
					// ii. If next is false, return undefined.
					if (next == null) return setCompleted();

					// iii. Let value be ? IteratorValue(next).
					final Value<?> value = iteratorValue(interpreter, next);
					// iv. Let selected be Completion(Call(predicate, undefined, « value, 𝔽(counter) »)).
					final Value<?> selected = predicate.call(interpreter, Undefined.instance, value, new NumberValue(counter));
					// FIXME: v. IfAbruptCloseIterator(selected, iterated).

					// vii. Set counter to counter + 1.
					counter = counter + 1;

					// vi. If ToBoolean(selected) is true, then
					if (selected.isTruthy(interpreter)) {
						// 1. Let completion be Completion(Yield(value)).
						return value;
						// FIXME: 2. IfAbruptCloseIterator(completion, iterated).
					}
				}
			}
		};
	}

	@SpecificationURL("https://tc39.es/proposal-iterator-helpers#sec-iteratorprototype.take")
	private static IteratorObject take(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 3.1.3.4 Iterator.prototype.take ( limit )
		final Value<?> limit = argument(0, arguments);

		// 1. Let O be the `this` value.
		// 2. If O is not an Object, throw a TypeError exception.
		final ObjectValue O = requireIteratorLike(interpreter, "take");
		// 3. Let numLimit be ? ToNumber(limit).
		final NumberValue numLimit = limit.toNumberValue(interpreter);
		// 4. If numLimit is NaN, throw a RangeError exception.
		if (numLimit.value.isNaN()) throw error(new RangeError(interpreter, "Limit must not be NaN"));
		// 5. Let integerLimit be ! ToIntegerOrInfinity(numLimit).
		final int integerLimit = toIntegerOrInfinity(interpreter, numLimit);
		// 6. If integerLimit < 0, throw a RangeError exception.
		if (integerLimit < 0) throw error(new RangeError(interpreter, "Limit must not be negative"));
		// 7. Let iterated be ? GetIteratorDirect(O).
		final IteratorRecord iterated = getIteratorDirect(interpreter, O);
		// TODO: 8. Let closure be a new Abstract Closure with no parameters that captures iterated and integerLimit and performs the following steps when called:
		// TODO: 9. Let result be CreateIteratorFromClosure(closure, "Iterator Helper", %IteratorHelperPrototype%, « [[UnderlyingIterator]] »).
		// TODO: 10. Set result.[[UnderlyingIterator]] to iterated.
		// 11. Return result.
		return new IteratorObject(interpreter.intrinsics) {
			// a. Let remaining be integerLimit.
			private int remaining = integerLimit;

			// b. Repeat,
			@Override
			public Value<?> next(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
				// i. If remaining is 0, then
				if (remaining == 0) {
					// TODO: 1. Return ? IteratorClose(iterated, NormalCompletion(undefined)).
					setCompleted();
					return Undefined.instance;
				}

				// ii. If remaining is not +∞, then
				if (remaining != Integer.MAX_VALUE) {
					// 1. Set remaining to remaining - 1.
					remaining = remaining - 1;
				}

				// iii. Let next be ? IteratorStep(iterated).
				final ObjectValue next = iterated.step(interpreter);
				// iv. If next is false, return undefined.
				if (next == null) return setCompleted();

				// v. Let completion be Completion(Yield(? IteratorValue(next))).
				return iteratorValue(interpreter, next);
				// FIXME: vi. IfAbruptCloseIterator(completion, iterated).
			}
		};
	}

	@SpecificationURL("https://tc39.es/proposal-iterator-helpers#sec-iteratorprototype.drop")
	private static IteratorObject drop(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 3.1.3.5 Iterator.prototype.drop ( limit )
		final Value<?> limit = argument(0, arguments);

		// 1. Let O be the `this` value.
		// 2. If O is not an Object, throw a TypeError exception.
		final ObjectValue O = requireIteratorLike(interpreter, "drop");
		// 3. Let numLimit be ? ToNumber(limit).
		final NumberValue numLimit = limit.toNumberValue(interpreter);
		// 4. If numLimit is NaN, throw a RangeError exception.
		if (numLimit.value.isNaN()) throw error(new RangeError(interpreter, "Limit must not be NaN"));
		// 5. Let integerLimit be ! ToIntegerOrInfinity(numLimit).
		final int integerLimit = toIntegerOrInfinity(interpreter, numLimit);
		// 6. If integerLimit < 0, throw a RangeError exception.
		if (integerLimit < 0) throw error(new RangeError(interpreter, "Limit must not be negative"));
		// 7. Let iterated be ? GetIteratorDirect(O).
		final IteratorRecord iterated = getIteratorDirect(interpreter, O);
		// TODO: 8. Let closure be a new Abstract Closure with no parameters that captures iterated and integerLimit and performs the following steps when called:
		// TODO: 9. Let result be CreateIteratorFromClosure(closure, "Iterator Helper", %IteratorHelperPrototype%, « [[UnderlyingIterator]] »).
		// TODO: 10. Set result.[[UnderlyingIterator]] to iterated.
		// 11. Return result.
		return new IteratorObject(interpreter.intrinsics) {
			// a. Let remaining be integerLimit.
			private int remaining = integerLimit;


			@Override
			public Value<?> next(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
				// b. Repeat, while remaining > 0,
				while (remaining > 0) {
					// i. If remaining is not +∞, then
					if (remaining != Integer.MAX_VALUE) {
						// 1. Set remaining to remaining - 1.
						remaining = remaining - 1;
					}

					// ii. Let next be ? IteratorStep(iterated).
					final ObjectValue next = iterated.step(interpreter);
					// iii. If next is false, return undefined.
					if (next == null) return setCompleted();
				}

				// c. Repeat,
				// i. Let next be ? IteratorStep(iterated).
				final ObjectValue next = iterated.step(interpreter);
				// ii. If next is false, return undefined.
				if (next == null) return setCompleted();

				// iii. Let completion be Completion(Yield(? IteratorValue(next))).
				return iteratorValue(interpreter, next);
				// FIXME: iv. IfAbruptCloseIterator(completion, iterated).
			}
		};
	}

	@SpecificationURL("https://tc39.es/proposal-iterator-helpers#sec-iteratorprototype.flatMap")
	private static Value<?> flatMap(Interpreter interpreter, Value<?>[] arguments) {
		// 3.1.3.6 Iterator.prototype.flatMap ( mapper )
		final Value<?> mapper_ = argument(0, arguments);

		throw new NotImplemented("Iterator.prototype.flatMap()");
	}

	@SpecificationURL("https://tc39.es/proposal-iterator-helpers#sec-iteratorprototype.reduce")
	private static Value<?> reduce(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 3.1.3.7 Iterator.prototype.reduce ( reducer [ , initialValue ] )
		final Value<?> reducer_ = argument(0, arguments);
		final Value<?> initialValue = argument(1, arguments);

		// 1. Let O be the `this` value.
		// 2. If O is not an Object, throw a TypeError exception.
		final ObjectValue O = requireIteratorLike(interpreter, "reduce");
		// 3. If IsCallable(reducer) is false, throw a TypeError exception.
		final Executable reducer = Executable.getExecutable(interpreter, reducer_);
		// 4. Let iterated be ? GetIteratorDirect(O).
		final IteratorRecord iterated = getIteratorDirect(interpreter, O);
		// 5. If initialValue is not present, then
		Value<?> accumulator;
		int counter;
		if (arguments.length == 1) {
			// a. Let next be ? IteratorStep(iterated).
			final ObjectValue next = iterated.step(interpreter);
			// b. If next is false, throw a TypeError exception.
			if (next == null) throw error(new TypeError(interpreter, "Iterator has finished"));
			// c. Let accumulator be ? IteratorValue(next).
			accumulator = iteratorValue(interpreter, next);
			// d. Let counter be 1.
			counter = 1;
		}
		// 6. Else,
		else {
			// a. Let accumulator be initialValue.
			accumulator = initialValue;
			// b. Let counter be 0.
			counter = 0;
		}
		// 7. Repeat,
		while (true) {
			// a. Let next be ? IteratorStep(iterated).
			final ObjectValue next = iterated.step(interpreter);
			// b. If next is false, return accumulator.
			if (next == null) return accumulator;
			// c. Let value be ? IteratorValue(next).
			final Value<?> value = iteratorValue(interpreter, next);
			// d. Let result be Completion(Call(reducer, undefined, « accumulator, value, 𝔽(counter) »)).
			final Value<?> result = reducer.call(interpreter, Undefined.instance, accumulator, value, new NumberValue(counter));
			// TODO: e. IfAbruptCloseIterator(result, iterated).
			// f. Set accumulator to result.[[Value]].
			accumulator = result;
			// g. Set counter to counter + 1.
			counter = counter + 1;
		}
	}

	@SpecificationURL("https://tc39.es/proposal-iterator-helpers#sec-iteratorprototype.toArray")
	private static ArrayObject toArray(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 3.1.3.8 Iterator.prototype.toArray ( )

		// 1. Let O be the `this` value.
		// 2. If O is not an Object, throw a TypeError exception.
		final ObjectValue O = requireIteratorLike(interpreter, "toArray");
		// 3. Let iterated be ? GetIteratorDirect(O).
		final IteratorRecord iterated = getIteratorDirect(interpreter, O);
		// 4. Let items be a new empty List.
		final ArrayList<Value<?>> items = new ArrayList<>();
		// 5. Repeat,
		while (true) {
			// a. Let next be ? IteratorStep(iterated).
			final ObjectValue next = iterated.step(interpreter);
			// b. If next is false, return CreateArrayFromList(items).
			if (next == null) return new ArrayObject(interpreter, items);
			// c. Let value be ? IteratorValue(next).
			final Value<?> value = iteratorValue(interpreter, next);
			// d. Append value to items.
			items.add(value);
		}
	}

	@SpecificationURL("https://tc39.es/proposal-iterator-helpers#sec-iteratorprototype.forEach")
	private static Undefined forEach(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 3.1.3.9 Iterator.prototype.forEach ( fn )
		final Value<?> fn_ = argument(0, arguments);

		// 1. Let O be the `this` value.
		// 2. If O is not an Object, throw a TypeError exception.
		final ObjectValue O = requireIteratorLike(interpreter, "forEach");
		// 3. If IsCallable(fn) is false, throw a TypeError exception.
		final Executable fn = Executable.getExecutable(interpreter, fn_);
		// 4. Let iterated be ? GetIteratorDirect(O).
		final IteratorRecord iterated = getIteratorDirect(interpreter, O);
		// 5. Let counter be 0.
		int counter = 0;
		// 6. Repeat,
		while (true) {
			// a. Let next be ? IteratorStep(iterated).
			final ObjectValue next = iterated.step(interpreter);
			// b. If next is false, return undefined.
			if (next == null) return Undefined.instance;
			// c. Let value be ? IteratorValue(next).
			final Value<?> value = iteratorValue(interpreter, next);
			// d. Let result be Completion(Call(fn, undefined, « value, 𝔽(counter) »)).
			final Value<?> result = fn.call(interpreter, Undefined.instance, value, new NumberValue(counter));
			// TODO: e. IfAbruptCloseIterator(result, iterated).
			// f. Set counter to counter + 1.
			counter = counter + 1;
		}
	}

	@SpecificationURL("https://tc39.es/proposal-iterator-helpers#sec-iteratorprototype.some")
	private static BooleanValue some(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 3.1.3.10 Iterator.prototype.some ( predicate )
		final Value<?> predicate_ = argument(0, arguments);

		// 1. Let O be the `this` value.
		// 2. If O is not an Object, throw a TypeError exception.
		final ObjectValue O = requireIteratorLike(interpreter, "some");
		// 3. If IsCallable(predicate) is false, throw a TypeError exception.
		final Executable predicate = Executable.getExecutable(interpreter, predicate_);
		// 4. Let iterated be ? GetIteratorDirect(O).
		final IteratorRecord iterated = getIteratorDirect(interpreter, O);
		// 5. Let counter be 0.
		int counter = 0;
		// 6. Repeat,
		while (true) {
			// a. Let next be ? IteratorStep(iterated).
			final ObjectValue next = iterated.step(interpreter);
			// b. If next is false, return false.
			if (next == null) return BooleanValue.FALSE;
			// c. Let value be ? IteratorValue(next).
			final Value<?> value = iteratorValue(interpreter, next);
			// d. Let result be Completion(Call(predicate, undefined, « value, 𝔽(counter) »)).
			final Value<?> result = predicate.call(interpreter, Undefined.instance, value, new NumberValue(counter));
			// TODO: e. IfAbruptCloseIterator(result, iterated).
			// TODO: f. If ToBoolean(result) is true, return ? IteratorClose(iterated, NormalCompletion(true)).
			if (result.isTruthy(interpreter)) return BooleanValue.TRUE;
			// g. Set counter to counter + 1.
			counter = counter + 1;
		}
	}

	@SpecificationURL("https://tc39.es/proposal-iterator-helpers#sec-iteratorprototype.every")
	private static BooleanValue every(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 3.1.3.11 Iterator.prototype.every ( predicate )
		final Value<?> predicate_ = argument(0, arguments);

		// 1. Let O be the `this` value.
		// 2. If O is not an Object, throw a TypeError exception.
		final ObjectValue O = requireIteratorLike(interpreter, "every");
		// 3. If IsCallable(predicate) is false, throw a TypeError exception.
		final Executable predicate = Executable.getExecutable(interpreter, predicate_);
		// 4. Let iterated be ? GetIteratorDirect(O).
		final IteratorRecord iterated = getIteratorDirect(interpreter, O);
		// 5. Let counter be 0.
		int counter = 0;
		// 6. Repeat,
		while (true) {
			// a. Let next be ? IteratorStep(iterated).
			final ObjectValue next = iterated.step(interpreter);
			// b. If next is false, return true.
			if (next == null) return BooleanValue.TRUE;
			// c. Let value be ? IteratorValue(next).
			final Value<?> value = iteratorValue(interpreter, next);
			// d. Let result be Completion(Call(predicate, undefined, « value, 𝔽(counter) »)).
			final Value<?> result = predicate.call(interpreter, Undefined.instance, value, new NumberValue(counter));
			// TODO: e. IfAbruptCloseIterator(result, iterated).
			// TODO: f. If ToBoolean(result) is false, return ? IteratorClose(iterated, NormalCompletion(false)).
			if (!result.isTruthy(interpreter)) return BooleanValue.FALSE;
			// g. Set counter to counter + 1.
			counter = counter + 1;
		}
	}

	@SpecificationURL("https://tc39.es/proposal-iterator-helpers#sec-iteratorprototype.find")
	private static Value<?> find(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 3.1.3.12 Iterator.prototype.find ( predicate )
		final Value<?> predicate_ = argument(0, arguments);

		// 1. Let O be the `this` value.
		// 2. If O is not an Object, throw a TypeError exception.
		final ObjectValue O = requireIteratorLike(interpreter, "find");
		// 3. If IsCallable(predicate) is false, throw a TypeError exception.
		final Executable predicate = Executable.getExecutable(interpreter, predicate_);
		// 4. Let iterated be ? GetIteratorDirect(O).
		final IteratorRecord iterated = getIteratorDirect(interpreter, O);
		// 5. Let counter be 0.
		int counter = 0;
		// 6. Repeat,
		while (true) {
			// a. Let next be ? IteratorStep(iterated).
			final ObjectValue next = iterated.step(interpreter);
			// b. If next is false, return undefined.
			if (next == null) return Undefined.instance;
			// c. Let value be ? IteratorValue(next).
			final Value<?> value = iteratorValue(interpreter, next);
			// d. Let result be Completion(Call(predicate, undefined, « value, 𝔽(counter) »)).
			final Value<?> result = predicate.call(interpreter, Undefined.instance, value, new NumberValue(counter));
			// TODO: e. IfAbruptCloseIterator(result, iterated).
			// TODO: f. If ToBoolean(result) is true, return ? IteratorClose(iterated, NormalCompletion(value)).
			if (result.isTruthy(interpreter)) return value;
			// g. Set counter to counter + 1.
			counter = counter + 1;
		}
	}
}
