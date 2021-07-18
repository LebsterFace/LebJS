package xyz.lebster.runtime;

import xyz.lebster.ANSI;
import xyz.lebster.node.value.*;

public final class ConsoleObject extends Dictionary {
	public static final ConsoleObject instance = new ConsoleObject();

	private ConsoleObject() {
		set("log", new NativeFunction(((interpreter, data) -> {
			logger(LogLevel.Log, data);
			return new Undefined();
		})));

		set("warn", new NativeFunction(((interpreter, data) -> {
			logger(LogLevel.Warn, data);
			return new Undefined();
		})));

		set("error", new NativeFunction(((interpreter, data) -> {
			logger(LogLevel.Error, data);
			return new Undefined();
		})));

		set("info", new NativeFunction(((interpreter, data) -> {
			logger(LogLevel.Info, data);
			return new Undefined();
		})));
	}

	private void logger(LogLevel logLevel, Value<?>[] args) {
		if (args.length == 0) return;

		if (args.length == 1) {
			printer(logLevel, args[0]);
		} else {
			printer(logLevel, hasNoFormatters(args[0]) ? args : formatter(args));
		}
	}

	private boolean hasNoFormatters(Value<?> arg) {
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
		for (int i = 0; i < args.length; i++) strings[i] = args[i].toStringLiteral().value;
		System.out.print(String.join(", ", strings));

		System.out.println(ANSI.RESET);
	}

	private enum LogLevel {
		Log, Info, Warn, Error
	}
}