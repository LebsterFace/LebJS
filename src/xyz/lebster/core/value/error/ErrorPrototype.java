package xyz.lebster.core.value.error;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-properties-of-the-error-prototype-object")
public class ErrorPrototype extends ObjectValue {
	public ErrorPrototype(Intrinsics intrinsics) {
		super(intrinsics);
		putMethod(intrinsics, Names.toString, ErrorPrototype::toStringMethod);
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#sec-error.prototype.tostring")
	private static StringValue toStringMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// 1. Let O be the `this` value.
		// 2. If O is not an Object, throw a TypeError exception.
		if (!(interpreter.thisValue() instanceof final ObjectValue O))
			throw error(new TypeError(interpreter, "Error.prototype.toString requires that 'this' be an Object"));

		// 3. Let name be ? Get(O, "name").
		final Value<?> nameProperty = O.get(interpreter, Names.name);
		// 4. If name is undefined, set name to "Error"; otherwise set name to ? ToString(name).
		final StringValue name = nameProperty == Undefined.instance ? Names.Error : nameProperty.toStringValue(interpreter);
		// 5. Let msg be ? Get(O, "message").
		final Value<?> msgProperty = O.get(interpreter, Names.message);
		// 6. If msg is undefined, set msg to the empty String; otherwise set msg to ? ToString(msg).
		final StringValue msg = msgProperty == Undefined.instance ? StringValue.EMPTY : msgProperty.toStringValue(interpreter);
		// 7. If name is the empty String, return msg.
		if (name.value.isEmpty()) return msg;
		// 8. If msg is the empty String, return name.
		if (msg.value.isEmpty()) return name;
		// 9. Return the string-concatenation of name, the code unit 0x003A (COLON), the code unit 0x0020 (SPACE), and msg.
		return new StringValue(name.value + ": " + msg.value);

	}
}
