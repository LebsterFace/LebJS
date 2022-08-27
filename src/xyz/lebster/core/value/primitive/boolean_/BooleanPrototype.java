package xyz.lebster.core.value.primitive.boolean_;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.TypeError;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.object.ObjectPrototype;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-boolean-prototype-object")
public final class BooleanPrototype extends ObjectValue {
	public BooleanPrototype(ObjectPrototype objectPrototype, FunctionPrototype fp) {
		super(objectPrototype);
		this.putMethod(fp, Names.toString, BooleanPrototype::toStringMethod);
		this.putMethod(fp, Names.valueOf, BooleanPrototype::valueOf);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#thisbooleanvalue")
	private static BooleanValue thisBooleanValue(Interpreter interpreter, String methodName) throws AbruptCompletion {
		final Value<?> value = interpreter.thisValue();

		// 1. If Type(value) is Boolean, return value.
		if (value instanceof final BooleanValue result) return result;

		// 2. If Type(value) is Object and value has a [[BooleanData]] internal slot, then
		if (value instanceof final BooleanWrapper wrapper) {
			// a. Let b be value.[[BooleanData]].
			// b. Assert: Type(b) is Boolean.
			// c. Return b.
			return wrapper.data;
		}

		// 3. Throw a TypeError exception.
		final String message = "Boolean.prototype.%s requires that 'this' be a Boolean".formatted(methodName);
		throw AbruptCompletion.error(new TypeError(interpreter, message));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-boolean.prototype.valueof")
	private static BooleanValue valueOf(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.3.3.3 Boolean.prototype.valueOf ( )

		// 1. Return ? thisBooleanValue(this value).
		return thisBooleanValue(interpreter, "valueOf");
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-boolean.prototype.tostring")
	private static StringValue toStringMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 20.3.3.2 Boolean.prototype.toString ( )

		// 1. Let b be ? thisBooleanValue(this value).
		final BooleanValue b = thisBooleanValue(interpreter, "toString");
		// 2. If b is true, return "true"; else return "false".
		return b.stringValue;
	}
}