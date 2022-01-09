package xyz.lebster.core.runtime.object;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.node.value.UndefinedValue;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.node.value.object.ObjectValue;

@SpecificationURL("https://console.spec.whatwg.org/")
public final class ConsoleObject extends ObjectValue {
	public static final ConsoleObject instance = new ConsoleObject();

	private ConsoleObject() {
		setMethod("log", (interpreter, data) -> {
			logger(LogLevel.Log, data);
			return UndefinedValue.instance;
		});

		setMethod("warn", (interpreter, data) -> {
			logger(LogLevel.Warn, data);
			return UndefinedValue.instance;
		});

		setMethod("error", (interpreter, data) -> {
			logger(LogLevel.Error, data);
			return UndefinedValue.instance;
		});

		setMethod("info", (interpreter, data) -> {
			logger(LogLevel.Info, data);
			return UndefinedValue.instance;
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
		final StringBuilder builder = new StringBuilder();

		builder.append(switch (logLevel) {
			case Log -> ANSI.RESET;
			case Info -> ANSI.BRIGHT_BLUE;
			case Warn -> ANSI.BRIGHT_YELLOW;
			case Error -> ANSI.BRIGHT_RED;
		});

		for (final Value<?> arg : args) {
			builder.append(arg.toConsoleLogString());
			builder.append(' ');
		}

		builder.append(ANSI.RESET);
		System.out.println(builder);
	}

	private enum LogLevel {
		Log, Info, Warn, Error
	}
}