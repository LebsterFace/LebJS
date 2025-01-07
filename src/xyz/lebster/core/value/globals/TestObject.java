package xyz.lebster.core.value.globals;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.NonStandard;
import xyz.lebster.core.StringEscapeUtils;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.object.Key;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.PrimitiveWrapper;
import xyz.lebster.core.value.primitive.boolean_.BooleanValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import static xyz.lebster.core.value.function.NativeFunction.argument;

@NonStandard
public final class TestObject extends ObjectValue {
	public TestObject(Intrinsics intrinsics) {
		super(Null.instance);

		putMethod(intrinsics, Names.expect, 2, TestObject::expect);
		putMethod(intrinsics, Names.equals, 2, TestObject::equalsMethod);
		putMethod(intrinsics, Names.expectEquals, 2, TestObject::expectEquals);
		putMethod(intrinsics, Names.fail, 0, TestObject::fail);
		putMethod(intrinsics, Names.expectError, 3, TestObject::expectError);
		putMethod(intrinsics, Names.parse, 1, TestObject::parse);
	}

	private static Undefined parse(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// Test.parse(sourceText: string): void
		if (arguments.length != 1)
			throw new ShouldNotHappen("Test.parse should be called with only one argument");
		if (!(arguments[0] instanceof final StringValue sourceTextSV))
			throw new ShouldNotHappen("Test.parse not called with a string");

		interpreter.runtimeParse(sourceTextSV.value);
		return Undefined.instance;
	}

	private static Undefined expectError(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// Test.expectError(name: string, messageStarter: string, callback: () => unknown): void
		final StringValue name = argument(0, arguments).toStringValue(interpreter);
		final StringValue messageStarter = argument(1, arguments).toStringValue(interpreter);
		final Value<?> potentialCallback = argument(2, arguments);

		if (!(potentialCallback instanceof final Executable callback))
			throw new ShouldNotHappen("Test.expectError called without callback");
		try {
			callback.call(interpreter, interpreter.globalObject);
		} catch (AbruptCompletion completion) {
			if (completion.type != AbruptCompletion.Type.Throw) throw completion;
			if (completion.value.isNullish()) throw new ShouldNotHappen("Thrown value was nullish");

			final ObjectValue error = completion.value.toObjectValue(interpreter);
			final StringValue nameProperty = error.get(interpreter, Names.name).toStringValue(interpreter);
			final StringValue messageProperty = error.get(interpreter, Names.message).toStringValue(interpreter);
			TestObject.expect(interpreter, name, nameProperty);
			if (!messageProperty.value.contains(messageStarter.value))
				throw assertionFailed(messageStarter, messageProperty);


			return Undefined.instance;
		}

		throw new ShouldNotHappen("Callback did not throw. Expecting " + StringEscapeUtils.quote(name.value + ": " + messageStarter.value, true));
	}

	private static Undefined expectEquals(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// Test.expectEquals(a: unknown, b: unknown): void
		final Value<?> a = argument(0, arguments);
		final Value<?> b = argument(1, arguments);

		if (equals(interpreter, a, b)) {
			return Undefined.instance;
		} else {
			throw assertionFailed(a, b);
		}
	}

	private static BooleanValue equalsMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// Test.equals(a: unknown, b: unknown): void
		final Value<?> a = argument(0, arguments);
		final Value<?> b = argument(1, arguments);

		return BooleanValue.of(equals(interpreter, a, b));
	}

	private static boolean equals(Interpreter interpreter, Value<?> a, Value<?> b) throws AbruptCompletion {
		if (
			a instanceof final ArrayObject expected &&
			b instanceof final ArrayObject received
		) {
			final Value<?>[] expectedValues = expected.values(interpreter);
			final Value<?>[] receivedValues = received.values(interpreter);
			if (expectedValues.length != receivedValues.length) return false;
			for (int i = 0; i < expectedValues.length; i++) {
				final Value<?> expectedElement = expectedValues[i];
				final Value<?> receivedElement = receivedValues[i];
				// NOTE: expectedElement can be `null` if the expected array contains holes
				if (expectedElement == null || receivedElement == null) {
					if (expectedElement != null || receivedElement != null)
						return false;
				} else if (!equals(interpreter, expectedElement, receivedElement)) {
					return false;
				}
			}

			return true;
		} else if (
			a.getClass() == ObjectValue.class &&
			b.getClass() == ObjectValue.class
		) {
			final ObjectValue expected = (ObjectValue) a;
			final ObjectValue received = (ObjectValue) b;
			if (expected.getPrototype() != received.getPrototype()) return false;
			for (final Key<?> expectedKey : expected.ownPropertyKeys()) {
				if (!received.hasOwnProperty(expectedKey)) return false;
				final var expectedValue = expected.get(interpreter, expectedKey);
				final var recievedValue = received.get(interpreter, expectedKey);
				if (!equals(interpreter, expectedValue, recievedValue)) return false;
			}

			for (final Key<?> recievedKey : received.ownPropertyKeys()) {
				if (!expected.hasOwnProperty(recievedKey)) return false;
			}

			return true;
		} else if (
			a instanceof final PrimitiveWrapper<?,?> expected &&
			b instanceof final PrimitiveWrapper<?,?> received &&
			equals(interpreter, expected.getPrototype(), received.getPrototype()) &&
			equals(interpreter, expected.data, received.data)
		) {
			return true;
		} else {
			return a.equals(b);
		}
	}

	private static Undefined expect(Interpreter interpreter, Value<?>... arguments) {
		// Test.expect(a: unknown, b: unknown): void
		if (arguments.length != 2) throw new ShouldNotHappen("Test.expect() must be called with exactly two arguments");
		return expect(arguments[0], arguments[1]);
	}

	private static Undefined expect(Value<?> a, Value<?> b) {
		if (!a.equals(b)) {
			throw assertionFailed(a, b);
		}

		return Undefined.instance;
	}

	private static ShouldNotHappen assertionFailed(Value<?> expected, Value<?> received) {
		System.out.printf("%sExpected: %s\n%sReceived: %s%n", ANSI.BRIGHT_MAGENTA, displayExplicit(expected), ANSI.BRIGHT_MAGENTA, displayExplicit(received));
		return new ShouldNotHappen("Assertion failed.");
	}

	private static String displayExplicit(Value<?> value) {
		return "%s%s %s%s%s".formatted(ANSI.CYAN, value.getClass().getSimpleName(), ANSI.RESET, value.toDisplayString(false), ANSI.RESET);
	}

	private static Undefined fail(Interpreter interpreter, Value<?>[] values) {
		// Test.fail(): void
		throw new ShouldNotHappen("Test.fail()");
	}
}
