package xyz.lebster;

import xyz.lebster.cli.CommandLineArguments;
import xyz.lebster.cli.ScriptExecutor;
import xyz.lebster.cli.Testing;
import xyz.lebster.core.interpreter.Interpreter;

import java.io.PrintStream;
import java.nio.file.Path;

public final class Main {
	public static final PrintStream stdout = System.out;

	public static void main(String[] args) {
		CommandLineArguments arguments = null;

		try {
			arguments = CommandLineArguments.from(args);
		} catch (IllegalArgumentException e) {
			ScriptExecutor.handleError(false, e, System.out);
			return;
		}

		switch (arguments.mode()) {
			case Tests -> Testing.test(arguments.options());
			case REPL -> ScriptExecutor.repl(arguments.options());
			case File -> ScriptExecutor.executeFileWithHandling(Path.of(arguments.fileName()), new Interpreter(), arguments.options());
			default -> throw new Error("Mode could not be inferred");
		}
	}
}