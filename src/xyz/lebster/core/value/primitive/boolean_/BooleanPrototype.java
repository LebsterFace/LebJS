package xyz.lebster.core.value.primitive.boolean_;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-boolean-prototype-object")
public final class BooleanPrototype extends ObjectValue {
	public BooleanPrototype(Intrinsics intrinsics) {
		super(intrinsics);
		putMethod(intrinsics, Names.toString, 1, BooleanPrototype::toStringMethod);
		putMethod(intrinsics, Names.valueOf, 1, BooleanPrototype::valueOf);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-boolean.prototype.valueof")
	private static BooleanValue valueOf(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.3.3.3 Boolean.prototype.valueOf ( )

		// 1. Return ? thisBooleanValue(this value).
		return thisBooleanValue(interpreter);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-boolean.prototype.tostring")
	private static StringValue toStringMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.3.3.2 Boolean.prototype.toString ( )

		// 1. Let b be ? thisBooleanValue(this value).
		final BooleanValue b = thisBooleanValue(interpreter);
		// 2. If b is true, return "true"; else return "false".
		return b.value ? Names.true_ : Names.false_;
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#thisbooleanvalue")
	private static BooleanValue thisBooleanValue(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> value = interpreter.thisValue();
		// 1. If value is a Boolean, return value.
		if (value instanceof BooleanValue booleanValue) return booleanValue;
		// 2. If value is an Object and value has a [[BooleanData]] internal slot, return value.[[BooleanData]].
		if (value instanceof BooleanWrapper booleanWrapper) return booleanWrapper.data;
		// 3. Throw a TypeError exception.
		throw error(interpreter.incompatibleReceiver("Boolean.prototype", "a Boolean"));
	}
}