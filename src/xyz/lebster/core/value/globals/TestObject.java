package xyz.lebster.core.value.globals;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.exception.CannotParse;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Realm;
import xyz.lebster.core.parser.StringEscapeUtils;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.string.StringValue;

import static xyz.lebster.core.value.function.NativeFunction.argument;

public final class TestObject extends ObjectValue {
	public TestObject(FunctionPrototype functionPrototype) {
		super(null);

		this.putMethod(functionPrototype, Names.expect, TestObject::expect);
		this.putMethod(functionPrototype, Names.equals, TestObject::equalsMethod);
		this.putMethod(functionPrototype, Names.fail, TestObject::fail);
		this.putMethod(functionPrototype, Names.expectError, TestObject::expectError);
		this.putMethod(functionPrototype, Names.parse, TestObject::parse);
	}

	private static Undefined parse(Interpreter interpreter, Value<?>[] arguments) {
		if (arguments.length != 1)
			throw new ShouldNotHappen("Test.parse should be called with only one argument");
		if (!(arguments[0] instanceof final StringValue sourceTextSV))
			throw new ShouldNotHappen("Test.parse not called with a string");

		try {
			Realm.parse(sourceTextSV.value);
		} catch (SyntaxError | CannotParse e) {
			throw new RuntimeException(e);
		}

		return Undefined.instance;
	}

	private static Undefined expectError(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		final StringValue name = argument(0, arguments).toStringValue(interpreter);
		final StringValue messageStarter = argument(1, arguments).toStringValue(interpreter);
		final Value<?> potentialCallback = argument(2, arguments);
		if (!(potentialCallback instanceof final Executable callback)) {
			throw new ShouldNotHappen("Test.expectError called without callback");
		}

		try {
			callback.call(interpreter, new Value[0]);
		} catch (AbruptCompletion completion) {
			if (completion.type != AbruptCompletion.Type.Throw) throw completion;
			if (completion.value.isNullish()) throw new ShouldNotHappen("Thrown value was nullish");

			final ObjectValue error = completion.value.toObjectValue(interpreter);
			final StringValue nameProperty = error.get(interpreter, Names.name).toStringValue(interpreter);
			final StringValue messageProperty = error.get(interpreter, Names.message).toStringValue(interpreter);
			TestObject.expect(interpreter, name, nameProperty);
			if (!messageProperty.value.startsWith(messageStarter.value))
				assertionFailed(messageStarter, messageProperty);


			return Undefined.instance;
		}

		throw new ShouldNotHappen("Callback did not throw. Expecting " + StringEscapeUtils.quote(name.value + ": " + messageStarter.value, true));
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

	private static Undefined expect(Interpreter interpreter, Value<?>... arguments) {
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
