package xyz.lebster.core.runtime.prototype;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.NumberValue;
import xyz.lebster.core.node.value.StringValue;
import xyz.lebster.core.node.value.UndefinedValue;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.node.value.object.Executable;
import xyz.lebster.core.node.value.object.ObjectValue;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.error.TypeError;
import xyz.lebster.core.runtime.object.ArrayObject;

public final class ArrayPrototype extends ObjectValue {
	public static final ArrayPrototype instance = new ArrayPrototype();
	public static final long MAX_LENGTH = 9007199254740991L; // 2^53 - 1

	static {
		instance.setMethod(Names.push, ArrayPrototype::push);
		instance.setMethod(Names.map, ArrayPrototype::map);
		instance.setMethod(Names.join, ArrayPrototype::join);
		instance.setMethod(Names.toString, ArrayPrototype::toStringMethod);
	}

	private ArrayPrototype() {
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.tostring")
	private static Value<?> toStringMethod(Interpreter interpreter, Value<?>[] values) throws AbruptCompletion {
		// 1. Let array be ? ToObject(this value).
		final ObjectValue array = interpreter.thisValue().toObjectValue(interpreter);
		// 2. Let func be ? Get(array, "join").
		final Value<?> func = array.get(Names.join);
		// 3. If IsCallable(func) is false, set func to the intrinsic function %Object.prototype.toString%.
		final Executable<?> f_Func = func instanceof Executable<?> e ? e : ObjectPrototype.toStringMethod;
		// 4. Return ? Call(func, array).
		return f_Func.call(interpreter, array);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.join")
	private static Value<?> join(Interpreter interpreter, Value<?>[] elements) throws AbruptCompletion {
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		final long len = Long.min(MAX_LENGTH, O.get(ArrayObject.LENGTH_KEY).toNumberValue(interpreter).value.longValue());
		final boolean noSeparator = elements.length == 0 || elements[0].type == Type.Undefined;
		final String sep = noSeparator ? "," : elements[0].toStringValue(interpreter).value;

		final StringBuilder result = new StringBuilder();
		for (int k = 0; k < len; k++) {
			if (k > 0) result.append(sep);
			final Value<?> element = O.get(new StringValue(k));
			result.append(element.isNullish() ? "" : element.toStringValue(interpreter).value);
		}

		return new StringValue(result.toString());
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-lengthofarraylike")
	private static long lengthOfArrayLike(ObjectValue O, Interpreter interpreter) throws AbruptCompletion {
		final double number = O.get(ArrayObject.LENGTH_KEY).toNumberValue(interpreter).value;
		if (Double.isNaN(number) || number <= 0) {
			return 0L;
		} else {
			return Long.min((long) number, MAX_LENGTH);
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.push")
	private static Value<?> push(Interpreter interpreter, Value<?>[] elements) throws AbruptCompletion {
		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		final long len = lengthOfArrayLike(O, interpreter);

		if ((len + elements.length) > MAX_LENGTH) {
			final String message = "Pushing " + elements.length + " elements on an array-like of length " + len +
								   " is disallowed, as the total surpasses 2^53-1";
			throw AbruptCompletion.error(new TypeError(message));
		}

		for (final Value<?> E : elements)
			O.set(interpreter, new StringValue(len), E);

		final NumberValue newLength = new NumberValue(len + elements.length);
		O.set(interpreter, ArrayObject.LENGTH_KEY, newLength);
		return newLength;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-array.prototype.map")
	private static Value<?> map(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		final Value<?> callbackfn = arguments.length > 0 ? arguments[0] : UndefinedValue.instance;
		final Value<?> thisArg = arguments.length > 1 ? arguments[1] : UndefinedValue.instance;

		final ObjectValue O = interpreter.thisValue().toObjectValue(interpreter);
		final long len = lengthOfArrayLike(O, interpreter);

		if (!(callbackfn instanceof final Executable<?> executable)) {
			final StringRepresentation builder = new StringRepresentation();
			callbackfn.display(builder);
			builder.append(" is not a function");
			throw AbruptCompletion.error(new TypeError(builder.toString()));
		}

		final Value<?>[] values = new Value<?>[(int) len];
		for (int k = 0; k < len; k++) {
			final var Pk = new StringValue(k);
			if (O.hasOwnProperty(Pk)) {
				values[k] = executable.call(interpreter, thisArg, O.get(Pk), new NumberValue(k), O);
			}
		}

		return new ArrayObject(values);
	}
}