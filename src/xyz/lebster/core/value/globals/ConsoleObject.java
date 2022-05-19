package xyz.lebster.core.value.globals;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.NonStandard;
import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.boolean_.BooleanValue;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.string.StringValue;

import java.util.Scanner;

import static xyz.lebster.core.value.function.NativeFunction.argumentString;

@SpecificationURL("https://console.spec.whatwg.org/")
public final class ConsoleObject extends ObjectValue {
	private static final Scanner scanner = new Scanner(System.in);

	public ConsoleObject(FunctionPrototype functionPrototype) {
		super(null);
		this.putMethod(functionPrototype, Names.write, ConsoleObject::write);
		this.putMethod(functionPrototype, Names.log, (interpreter, data) -> logger(LogLevel.Log, data));
		this.putMethod(functionPrototype, Names.warn, (interpreter, data) -> logger(LogLevel.Warn, data));
		this.putMethod(functionPrototype, Names.error, (interpreter, data) -> logger(LogLevel.Error, data));
		this.putMethod(functionPrototype, Names.info, (interpreter, data) -> logger(LogLevel.Info, data));
		this.putMethod(functionPrototype, Names.input, ConsoleObject::input);
	}

	private static Undefined write(Interpreter interpreter, Value<?>[] data) {
		// If args is empty, return.
		if (data.length == 0) return Undefined.instance;

		final var representation = new StringRepresentation();
		representation.append(ANSI.RESET);
		for (final Value<?> arg : data)
			arg.displayForConsoleLog(representation);
		representation.append(ANSI.RESET);
		System.out.print(representation);
		return Undefined.instance;
	}


	@NonStandard
	private static Value<?> input(Interpreter interpreter, Value<?>[] args) throws AbruptCompletion {
		System.out.print(argumentString(0, "", interpreter, args));
		final String inputType = argumentString(1, "", interpreter, args);
		final String input = scanner.nextLine();

		return switch (inputType.toUpperCase()) {
			default -> new StringValue(input);
			case "NUMBER" -> new StringValue(input).toNumberValue(interpreter);
			case "BOOLEAN" -> BooleanValue.of(input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("true") || input.equalsIgnoreCase("y"));
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