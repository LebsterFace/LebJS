package xyz.lebster;

import xyz.lebster.cli.CLArguments;
import xyz.lebster.core.ANSI;
import xyz.lebster.core.exception.CannotParse;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.Program;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.parser.Lexer;
import xyz.lebster.core.parser.Parser;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public final class ScriptExecutor {
	public static void error(Throwable throwable, PrintStream stream, boolean showStackTrace) {
		stream.print(ANSI.BRIGHT_RED);
		if (showStackTrace) {
			throwable.printStackTrace(stream);
			stream.print(ANSI.RESET);
			stream.flush();
			return;
		}

		if (
			throwable instanceof final AbruptCompletion abruptCompletion &&
			abruptCompletion.type == AbruptCompletion.Type.Throw
		) {
			stream.print("Uncaught ");
			stream.print(abruptCompletion.getValue());
		} else if (throwable instanceof CannotParse) {
			stream.print(throwable.getLocalizedMessage());
		} else {
			stream.print(throwable.getClass().getSimpleName());
			stream.print(": ");
			stream.print(throwable.getLocalizedMessage());
		}

		stream.println(ANSI.RESET);
	}

	public static void executeWithoutErrorHandling(String source, Interpreter interpreter, CLArguments.ExecutionOptions options) throws SyntaxError, CannotParse, AbruptCompletion {
		if (source.startsWith("// @opt: tokenize-only")) {
			new Lexer(source).tokenize();
			return;
		}

		final Program program = new Parser(new Lexer(source).tokenize()).parse();
		if (options.showAST()) {
			System.out.println("------- AST -------");
			program.dump(0);
			System.out.println("------- END -------");
		}

		final Value<?> lastValue = program.execute(interpreter);
		if (options.showLastValue())
			System.out.println(lastValue.toDisplayString());
	}

	public static void executeFileWithoutErrorHandling(Path path, CLArguments.ExecutionOptions options) throws CannotParse, AbruptCompletion, SyntaxError {
		String source;

		try {
			source = Files.readString(path);
		} catch (IOException e) {
			error(e, System.out, options.showStackTrace());
			return;
		}

		executeWithoutErrorHandling(source, new Interpreter(), options);
	}

	public static void file(Path path, CLArguments.ExecutionOptions options) {
		try {
			executeFileWithoutErrorHandling(path, options);
		} catch (SyntaxError | CannotParse | AbruptCompletion e) {
			error(e, System.out, options.showStackTrace());
		}
	}

	public static void gif(CLArguments.ExecutionOptions options) {
		final Scanner scanner = new Scanner(System.in);
		final Interpreter interpreter = new Interpreter();

		while (scanner.hasNextLine()) {
			try {
				executeWithoutErrorHandling(scanner.nextLine(), interpreter, options);
				System.out.print("#[END-OF-OUTPUT]#");
			} catch (Throwable e) {
				throw new Error(e);
			}
		}
	}
}