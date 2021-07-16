package xyz.lebster;

import xyz.lebster.cli.CommandLineArguments;
import xyz.lebster.cli.ExecutionOptions;
import xyz.lebster.cli.ScriptExecutor;
import xyz.lebster.cli.Testing;

import java.io.PrintStream;
import java.nio.file.Path;

import static xyz.lebster.cli.ScriptExecutor.*;

public class Main {
	public static final PrintStream stdout = System.out;

	public static void main(String[] args) {
		CommandLineArguments arguments = null;

		try {
			arguments = CommandLineArguments.from(args);
		} catch (IllegalArgumentException e) {
			ScriptExecutor.handleError(false, e, System.out);
			return;
		}

		final ExecutionOptions opts = arguments.options();
		final boolean testing = opts.testingMethods();


		switch (arguments.mode()) {
			case Tests -> Testing.test(arguments.options());
			case REPL -> repl(getInterpreter(testing), opts);
			case File -> executeFileWithHandling(Path.of(arguments.fileName()), getInterpreter(testing), opts);
			default -> throw new Error("Mode could not be inferred");
		}
	}
}