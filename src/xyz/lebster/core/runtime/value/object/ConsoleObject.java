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

@SpecificationURL("https://console.spec.whatwg.org/")
public final class ConsoleObject extends ObjectValue {
	public static final ConsoleObject instance = new ConsoleObject();

	private ConsoleObject() {
		this.putMethod(Names.log, (interpreter, data) -> {
			logger(LogLevel.Log, data);
			return Undefined.instance;
		});

		this.putMethod(Names.warn, (interpreter, data) -> {
			logger(LogLevel.Warn, data);
			return Undefined.instance;
		});

		this.putMethod(Names.error, (interpreter, data) -> {
			logger(LogLevel.Error, data);
			return Undefined.instance;
		});

		this.putMethod(Names.info, (interpreter, data) -> {
			logger(LogLevel.Info, data);
			return Undefined.instance;
		});

		this.putMethod(Names.input, ConsoleObject::input);
	}

	private static final Scanner scanner = new Scanner(System.in);
	@NonStandard
	private static Value<?> input(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
		if (args.length > 0) {
			System.out.print(args[0].toStringValue(interpreter).value);
		}

		final String inputType = args.length > 1 ? args[1].toStringValue(interpreter).value : "";
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
	private void logger(LogLevel logLevel, Value<?>[] args) {
		// If args is empty, return.
		if (args.length == 0) return;
		// Let first be args[0].
		// Let rest be all elements following first in args.
		// If rest is empty
		if (args.length == 1) {
			// perform Printer(logLevel, « first ») and return.
			printer(logLevel, args[0]);
		} else {
			printer(logLevel, args);
		}
	}

	private void printer(LogLevel logLevel, Value<?>... args) {
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
	}

	private enum LogLevel { Log, Info, Warn, Error }
}