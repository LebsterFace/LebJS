package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.primitive.Undefined;
import xyz.lebster.core.runtime.value.prototype.FunctionPrototype;

import static xyz.lebster.core.runtime.value.native_.NativeFunction.argument;

public final class TestObject extends ObjectValue {
	public TestObject(FunctionPrototype functionPrototype) {
		super(null);

		this.putMethod(functionPrototype, Names.expect, TestObject::expect);
		this.putMethod(functionPrototype, Names.equals, TestObject::equalsMethod);
		this.putMethod(functionPrototype, Names.fail, TestObject::fail);
	}

	private static Undefined equalsMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		final Value<?> a = argument(0, arguments);
		final Value<?> b = argument(1, arguments);

		if (
			a instanceof final ArrayObject expected &&
			b instanceof final ArrayObject received
		) {
			final Value<?>[] expectedValues = expected.values(interpreter);
			final Value<?>[] receivedValues = received.values(interpreter);
			if (expectedValues.length != receivedValues.length) assertionFailed(a, b);
			for (int i = 0; i < expectedValues.length; i++) {
				final Value<?> expectedElement = expectedValues[i];
				final Value<?> receivedElement = receivedValues[i];
				if (!expectedElement.equals(receivedElement)) assertionFailed(a, b);
			}

			return Undefined.instance;
		} else {
			return expect(a, b);
		}
	}

	private static Undefined expect(Interpreter interpreter, Value<?>[] arguments) {
		final Value<?> a = argument(0, arguments);
		final Value<?> b = argument(1, arguments);
		return expect(a, b);
	}

	private static Undefined expect(Value<?> a, Value<?> b) {
		if (!a.equals(b)) {
			assertionFailed(a, b);
		}

		return Undefined.instance;
	}

	private static void assertionFailed(Value<?> expected, Value<?> received) {
		System.out.println(
			ANSI.BRIGHT_MAGENTA + "Expected: " +
			ANSI.CYAN + expected.getClass().getSimpleName() + " " +
			ANSI.RESET + expected.toDisplayString() + ANSI.RESET +
			"\n" +
			ANSI.BRIGHT_MAGENTA + "Received: " +
			ANSI.CYAN + received.getClass().getSimpleName() + " " +
			ANSI.RESET + received.toDisplayString() + ANSI.RESET
		);

		throw new ShouldNotHappen("Assertion failed.");
	}

	private static Undefined fail(Interpreter interpreter, Value<?>[] values) {
		throw new ShouldNotHappen("Test.fail()");
	}
}
