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
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.error.EvalError;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public final class Main {
	private static final Charset[] supportedCharsets = { StandardCharsets.UTF_8, StandardCharsets.UTF_16 };

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
				case Tests -> new Testing(arguments).test();
			}
		} catch (Throwable e) {
			handleError(e, System.out, arguments.options().hideStackTrace());
		}
	}

	private static void file(CLArguments arguments) throws CannotParse, AbruptCompletion, SyntaxError {
		final String sourceText = Main.readFile(arguments.filePathOrNull());
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

	public static String readFile(Path path) {
		for (final Charset charset : supportedCharsets) {
			try {
				return Files.readString(path, charset);
			} catch (MalformedInputException e) {
				throw new RuntimeException("Cannot read " + path.getFileName() + ": Unsupported charset");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return "";
	}

	public static void handleError(Throwable throwable, PrintStream stream, boolean hideStackTrace) {
		stream.print(ANSI.BRIGHT_RED);
		if (!hideStackTrace) {
			throwable.printStackTrace(stream);
			stream.print(ANSI.RESET);
			stream.flush();
			return;
		}

		if (throwable instanceof final AbruptCompletion abruptCompletion && abruptCompletion.type == AbruptCompletion.Type.Throw) {
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