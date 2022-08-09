package xyz.lebster.core.value.array;

import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.BuiltinConstructor;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.boolean_.BooleanValue;
import xyz.lebster.core.value.error.RangeError;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.number.NumberValue;
import xyz.lebster.core.value.object.ObjectPrototype;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.string.StringValue;

import static xyz.lebster.core.value.function.NativeFunction.argument;
import static xyz.lebster.core.value.function.NativeFunction.argumentInt;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array-constructor")
public class ArrayConstructor extends BuiltinConstructor<ArrayObject, ArrayPrototype> {
	public ArrayConstructor(ObjectPrototype objectPrototype, FunctionPrototype functionPrototype) {
		super(objectPrototype, functionPrototype, Names.Array);
		this.putMethod(functionPrototype, Names.of, ArrayConstructor::of);
		this.putMethod(functionPrototype, Names.isArray, ArrayConstructor::isArray);
	}

	@NonCompliant
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-isarray")
	private static BooleanValue isArray(Interpreter interpreter, Value<?>[] arguments) {
		// 7.2.2 IsArray ( argument )
		final Value<?> argument = argument(0, arguments);

		return BooleanValue.of(argument instanceof ArrayObject);
	}

	@NonStandard
	private static Value<?> of(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		final int len = argumentInt(0, 0, interpreter, arguments);
		final Value<?> callbackFn = argument(1, arguments);
		final Value<?> thisArg = argument(2, arguments);

		final Executable executable = Executable.getExecutable(interpreter, callbackFn);
		final Value<?>[] result = new Value<?>[len];
		for (int k = 0; k < len; k++)
			result[k] = executable.call(interpreter, thisArg, new NumberValue(k));
		return new ArrayObject(interpreter, result);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-arraycreate")
	// FIXME: Make this the source of truth instead of ArrayObject's constructor
	private static ArrayObject arrayCreate(Interpreter interpreter, int length, ArrayPrototype proto) {
		// 10.4.2.2 ArrayCreate ( length [ , proto ] )

		// 1. If length > 2^32 - 1, throw a RangeError exception.
		// 2. If proto is not present, set proto to %Array.prototype%.
		if (proto == null) proto = interpreter.intrinsics.arrayPrototype;
		// 3. Let A be MakeBasicObject(« [[Prototype]], [[Extensible]] »).
		// 4. Set A.[[Prototype]] to proto.
		// 5. Set A.[[DefineOwnProperty]] as specified in 10.4.2.1.
		// 6. Perform ! OrdinaryDefineOwnProperty(A, "length", PropertyDescriptor { [[Value]]: 𝔽(length), [[Writable]]: true, [[Enumerable]]: false, [[Configurable]]: false }).
		// 7. Return A.
		return new ArrayObject(interpreter, length);
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array")
	public ArrayObject construct(Interpreter interpreter, Value<?>[] values, ObjectValue newTarget) throws AbruptCompletion {
		// 23.1.1.1 Array ( ...values )

		// 1. If NewTarget is undefined, let newTarget be the active function object; else let newTarget be NewTarget.
		// FIXME: 2. Let proto be ? GetPrototypeFromConstructor(newTarget, "%Array.prototype%").
		final ArrayPrototype proto = interpreter.intrinsics.arrayPrototype;
		// 3. Let numberOfArgs be the number of elements in values.
		final int numberOfArgs = values.length;
		// 4. If numberOfArgs = 0, then
		if (numberOfArgs == 0) {
			// a. Return ! ArrayCreate(0, proto).
			return ArrayConstructor.arrayCreate(interpreter, 0, proto);
		}
		// 5. Else if numberOfArgs = 1, then
		else if (numberOfArgs == 1) {
			// a. Let len be values[0].
			final Value<?> len = values[0];
			// b. Let array be ! ArrayCreate(0, proto).
			final ArrayObject array = arrayCreate(interpreter, 0, proto);

			NumberValue intLen;
			if (len instanceof final NumberValue len_number) {
				// i. Let intLen be ! ToUint32(len).
				intLen = new NumberValue(len_number.toUint32());
				// ii. If SameValueZero(intLen, len) is false, throw a RangeError exception.
				if (!NumberValue.sameValueZero(intLen, len_number)) throw AbruptCompletion.error(new RangeError(interpreter, "Invalid array length"));
			} else {
				// i. Perform ! CreateDataPropertyOrThrow(array, "0", len).
				array.set(interpreter, new StringValue(0), len);
				// ii. Let intLen be 1𝔽.
				intLen = new NumberValue(1);
			}

			// e. Perform ! Set(array, "length", intLen, true).
			array.set(interpreter, Names.length, intLen /* FIXME: true */);
			// f. Return array.
			return array;
		}
		// 6. Else,
		else {
			// a. Assert: numberOfArgs ≥ 2.
			// b. Let array be ? ArrayCreate(numberOfArgs, proto).
			final ArrayObject array = arrayCreate(interpreter, numberOfArgs, proto);
			// c. Let k be 0.

			// d. Repeat, while k < numberOfArgs,
			for (int k = 0; k < numberOfArgs; k++) {
				// i. Let Pk be ! ToString(𝔽(k)).
				final var Pk = new StringValue(k);
				// ii. Let itemK be values[k].
				// FIXME: iii. Perform ! CreateDataPropertyOrThrow(array, Pk, itemK).
				array.set(interpreter, Pk, values[k]);
			}

			// FIXME: e. Assert: The mathematical value of array's "length" property is numberOfArgs.
			// f. Return array.
			return array;
		}
	}

	@Override
	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array")
	public Value<?> call(Interpreter interpreter, Value<?>[] values) throws AbruptCompletion {
		// 23.1.1.1 Array ( ...values )

		// 1. If NewTarget is undefined, let newTarget be the active function object; else let newTarget be NewTarget.
		return construct(interpreter, values, this);
	}
}
