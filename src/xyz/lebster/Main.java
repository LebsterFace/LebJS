package xyz.lebster;

import xyz.lebster.cli.CLArgumentException;
import xyz.lebster.cli.CLArguments;
import xyz.lebster.cli.REPL;
import xyz.lebster.cli.Testing;
import xyz.lebster.core.ANSI;
import xyz.lebster.core.exception.CannotParse;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Realm;
import xyz.lebster.core.runtime.value.Value;
import xyz.lebster.core.runtime.value.error.EvalError;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Scanner;

public final class Main {
	public static void main(String[] args) {
		CLArguments arguments;
		try {
			arguments = CLArguments.from(args);
		} catch (CLArgumentException e) {
			System.err.print(ANSI.BRIGHT_RED);
			System.err.print("CLArgumentException: ");
			System.err.print(e.getMessage());
			System.err.println(ANSI.RESET);
			System.exit(1);
			arguments = null;
		}

		try {
			switch (arguments.mode()) {
				case File -> file(arguments);
				case GIF -> gif();
				case REPL -> new REPL(arguments.options()).run();
				case Tests -> Testing.test(arguments);
			}
		} catch (Throwable e) {
			handleError(e, System.out, arguments.options().hideStackTrace());
		}
	}

	private static void file(CLArguments arguments) throws IOException, CannotParse, AbruptCompletion, SyntaxError {
		final String sourceText = Files.readString(arguments.filePathOrNull());
		Realm.executeStatic(sourceText, arguments.options().showAST());
	}

	private static void gif() throws CannotParse, AbruptCompletion, SyntaxError {
		final Scanner scanner = new Scanner(System.in);
		final Realm realm = new Realm(new Interpreter());
		while (scanner.hasNextLine()) {
			final Value<?> lastValue = realm.execute(scanner.nextLine(), true);
			System.out.println(lastValue.toDisplayString());
			System.out.print("#[END-OF-OUTPUT]#");
		}
	}

	public static void handleError(Throwable throwable, PrintStream stream, boolean hideStackTrace) {
		stream.print(ANSI.BRIGHT_RED);
		if (!hideStackTrace) {
			throwable.printStackTrace(stream);
			stream.print(ANSI.RESET);
			stream.flush();
			return;
		}

		if (
			throwable instanceof final AbruptCompletion abruptCompletion &&
			abruptCompletion.type == AbruptCompletion.Type.Throw
		) {
			if (abruptCompletion.value instanceof final EvalError evalError) {
				handleError(evalError.wrappedThrowable, stream, false);
				return;
			}

			stream.print("Uncaught ");
			stream.print(abruptCompletion.getValue());
			stream.println(ANSI.RESET);
			return;
		}

		stream.print(throwable.getClass().getSimpleName());
		stream.print(": ");
		stream.print(throwable.getLocalizedMessage());
		stream.println(ANSI.RESET);
	}
}