package xyz.lebster.core.runtime;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.Dictionary;
import xyz.lebster.core.node.value.Undefined;
import xyz.lebster.core.node.value.Value;

@SpecificationURL("https://console.spec.whatwg.org/")
public final class ConsoleObject extends Dictionary {
	public static final ConsoleObject instance = new ConsoleObject();

	private ConsoleObject() {
		setMethod("log", (interpreter, data) -> {
			logger(LogLevel.Log, data);
			return Undefined.instance;
		});

		setMethod("warn", (interpreter, data) -> {
			logger(LogLevel.Warn, data);
			return Undefined.instance;
		});

		setMethod("error", (interpreter, data) -> {
			logger(LogLevel.Error, data);
			return Undefined.instance;
		});

		setMethod("info", (interpreter, data) -> {
			logger(LogLevel.Info, data);
			return Undefined.instance;
		});

		setMethod("dump", ConsoleObject::dump);
	}

	@NonStandard
	private static Undefined dump(Interpreter interpreter, Value<?>[] data) {
		for (final Value<?> val : data)
			val.dump(0);
		return Undefined.instance;
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
		System.out.print(switch (logLevel) {
			case Log -> ANSI.RESET;
			case Info -> ANSI.BRIGHT_BLUE;
			case Warn -> ANSI.BRIGHT_YELLOW;
			case Error -> ANSI.BRIGHT_RED;
		});

		final String[] strings = new String[args.length];
		for (int i = 0; i < args.length; i++) strings[i] = args[i].toStringForLogging();
		System.out.print(String.join(", ", strings));

		System.out.println(ANSI.RESET);
	}

	private enum LogLevel {
		Log, Info, Warn, Error
	}
}