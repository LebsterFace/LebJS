package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.primitive.Undefined;

@SpecificationURL("https://console.spec.whatwg.org/")
public final class ConsoleObject extends ObjectValue {
	public static final ConsoleObject instance = new ConsoleObject();

	private ConsoleObject() {
		putMethod("log", (interpreter, data) -> {
			logger(LogLevel.Log, data);
			return Undefined.instance;
		});

		putMethod("warn", (interpreter, data) -> {
			logger(LogLevel.Warn, data);
			return Undefined.instance;
		});

		putMethod("error", (interpreter, data) -> {
			logger(LogLevel.Error, data);
			return Undefined.instance;
		});

		putMethod("info", (interpreter, data) -> {
			logger(LogLevel.Info, data);
			return Undefined.instance;
		});
	}

	private void logger(LogLevel logLevel, Value<?>[] args) {
		// If args is empty, return.
		if (args.length == 0) return;
		// Let first be args[0].
		// Let rest be all elements following first in args.
		// If rest is empty
		if (args.length == 1) {
			// perform Printer(logLevel, « first ») and return.
			printer(logLevel, args[0]);
		} else if (doesNotContainFormatSpecifiers(args[0])) {
			// If first does not contain any format specifiers, perform Printer(logLevel, args).
			printer(logLevel, args);
		} else {
			// Otherwise, perform Printer(logLevel, Formatter(args)).
			printer(logLevel, formatter(args));
		}
	}

	private boolean doesNotContainFormatSpecifiers(Value<?> arg) {
		return true;
	}

	private Value<?>[] formatter(Value<?>[] args) {
		return args;
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

	private enum LogLevel {
		Log, Info, Warn, Error
	}
}