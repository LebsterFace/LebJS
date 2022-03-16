package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.primitive.BooleanValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.primitive.Undefined;

import java.util.Scanner;

import static xyz.lebster.core.runtime.value.native_.NativeFunction.argumentString;

@SpecificationURL("https://console.spec.whatwg.org/")
public final class ConsoleObject extends ObjectValue {
	public static final ConsoleObject instance = new ConsoleObject();
	private static final Scanner scanner = new Scanner(System.in);

	private ConsoleObject() {

		this.putMethod(Names.write, (interpreter, data) -> {
			// If args is empty, return.
			if (data.length == 0) return Undefined.instance;

			final var representation = new StringRepresentation();
			representation.append(ANSI.RESET);
			for (final Value<?> arg : data)
				arg.displayForConsoleLog(representation);
			representation.append(ANSI.RESET);
			System.out.print(representation);
			return Undefined.instance;
		});

		this.putMethod(Names.log, (interpreter, data) -> logger(LogLevel.Log, data));
		this.putMethod(Names.warn, (interpreter, data) -> logger(LogLevel.Warn, data));
		this.putMethod(Names.error, (interpreter, data) -> logger(LogLevel.Error, data));
		this.putMethod(Names.info, (interpreter, data) -> logger(LogLevel.Info, data));
		this.putMethod(Names.input, ConsoleObject::input);
	}

	@NonStandard
	private static Value<?> input(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
		System.out.print(argumentString(0, "", interpreter, args));
		final String inputType = argumentString(1, "", interpreter, args);
		final String input = scanner.nextLine();

		return switch (inputType.toUpperCase()) {
			default -> new StringValue(input);
			case "NUMBER" -> new StringValue(input).toNumberValue(interpreter);
			case "BOOLEAN" -> BooleanValue.of(
				input.equalsIgnoreCase("yes") ||
				input.equalsIgnoreCase("true") ||
				input.equalsIgnoreCase("y")
			);
		};
	}

	@NonStandard
	private Undefined logger(LogLevel logLevel, Value<?>[] args) {
		if (args.length == 0) return Undefined.instance;
		final var representation = new StringRepresentation();

		representation.append(switch (logLevel) {
			case Log -> ANSI.RESET;
			case Info -> ANSI.BRIGHT_BLUE;
			case Warn -> ANSI.BRIGHT_YELLOW;
			case Error -> ANSI.BRIGHT_RED;
		});

		for (final Value<?> arg : args) {
			arg.displayForConsoleLog(representation);
			representation.append(' ');
		}

		representation.append(ANSI.RESET);
		System.out.println(representation);
		return Undefined.instance;
	}

	private enum LogLevel { Log, Info, Warn, Error }
}