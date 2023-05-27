package xyz.lebster.core.value.globals;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.NonStandard;
import xyz.lebster.core.StringEscapeUtils;
import xyz.lebster.core.exception.CannotParse;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.interpreter.Realm;
import xyz.lebster.core.interpreter.environment.ExecutionContext;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.error.EvalError;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.util.Objects;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.function.NativeFunction.argument;

@NonStandard
public final class TestObject extends ObjectValue {
	public TestObject(Intrinsics intrinsics) {
		super(Null.instance);

		putMethod(intrinsics, Names.expect, 2, TestObject::expect);
		putMethod(intrinsics, Names.equals, 2, TestObject::equalsMethod);
		putMethod(intrinsics, Names.fail, 0, TestObject::fail);
		putMethod(intrinsics, Names.expectError, 3, TestObject::expectError);
		putMethod(intrinsics, Names.expectSyntaxError, 2, TestObject::expectSyntaxError);
		putMethod(intrinsics, Names.parse, 1, TestObject::parse);
	}

	private static Undefined parse(Interpreter interpreter, Value<?>[] arguments) {
		if (arguments.length != 1)
			throw new ShouldNotHappen("Test.parse should be called with only one argument");
		if (!(arguments[0] instanceof final StringValue sourceTextSV))
			throw new ShouldNotHappen("Test.parse not called with a string");

		try {
			Realm.parse(sourceTextSV.value, false);
		} catch (SyntaxError | CannotParse e) {
			throw new RuntimeException(e);
		}

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

	private static Undefined expectSyntaxError(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		final StringValue messageStarter = argument(0, arguments).toStringValue(interpreter);
		final StringValue sourceText = argument(1, arguments).toStringValue(interpreter);

		final ExecutionContext context = interpreter.pushContextWithNewEnvironment();
		try {
			Realm.executeWith(sourceText.value, interpreter);
		} catch (SyntaxError error) {
			if (!error.getMessage().startsWith(messageStarter.value))
				throw assertionFailed(messageStarter, new StringValue(error.getMessage()));

			return Undefined.instance;
		} catch (Throwable e) {
			throw error(new EvalError(interpreter, e));
		} finally {
			interpreter.exitExecutionContext(context);
		}

		throw new ShouldNotHappen("Callback did not throw. Expecting " + StringEscapeUtils.quote("SyntaxError: " + messageStarter.value, true));
	}

	private static Undefined equalsMethod(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// Test.equals(a: unknown, b: unknown): void
		final Value<?> a = argument(0, arguments);
		final Value<?> b = argument(1, arguments);

		if (equals(interpreter, a, b)) {
			return Undefined.instance;
		} else {
			throw assertionFailed(a, b);
		}
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
		System.out.println(
			ANSI.BRIGHT_MAGENTA + "Expected: " +
			ANSI.CYAN + expected.getClass().getSimpleName() + " " +
			ANSI.RESET + expected.toDisplayString() + ANSI.RESET +
			"\n" +
			ANSI.BRIGHT_MAGENTA + "Received: " +
			ANSI.CYAN + received.getClass().getSimpleName() + " " +
			ANSI.RESET + received.toDisplayString() + ANSI.RESET
		);

		return new ShouldNotHappen("Assertion failed.");
	}

	private static Undefined fail(Interpreter interpreter, Value<?>[] values) {
		// Test.fail(): void
		throw new ShouldNotHappen("Test.fail()");
	}
}
