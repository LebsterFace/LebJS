package xyz.lebster.cli;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.exception.CannotParse;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.interpreter.GlobalObject;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.Program;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.parser.Lexer;
import xyz.lebster.core.parser.Parser;
import xyz.lebster.core.parser.Token;
import xyz.lebster.core.runtime.ConsoleObject;
import xyz.lebster.core.runtime.MathObject;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class ScriptExecutor {
	public static Interpreter getInterpreter(boolean testingMethods) {
		return getInterpreter(32, testingMethods);
	}

	public static Interpreter getInterpreter(int stackSize, boolean testingMethods) {
		final GlobalObject globalObject = new GlobalObject();
		globalObject.set("console", ConsoleObject.instance);
		globalObject.set("Math", MathObject.instance);
		globalObject.set("globalThis", globalObject);
		if (testingMethods) Testing.addTestingMethods(globalObject);

		return new Interpreter(stackSize, globalObject);
	}

	public static void handleError(boolean showDebug, Throwable e, PrintStream stream) {
		stream.print(ANSI.RED);
		stream.print(e.getClass().getSimpleName());
		stream.print(": ");
		stream.println(e.getLocalizedMessage());
		if (showDebug) e.printStackTrace(stream);
		stream.print(ANSI.RESET);
	}

	public static Program parse(String source) throws SyntaxError, CannotParse {
		return new Parser(new Lexer(source).tokenize()).parse();
	}

	private static boolean handleOptions(String source, ExecutionOptions executionOptions) {
		if (source.startsWith("// @opt: tokenize-only")) {
			try {
				new Lexer(source).tokenize();
				return true;
			} catch (SyntaxError e) {
				return false;
			}
		} else if (source.startsWith("// @opt: dump-tokens")) {
			try {
				final Token[] tokens = new Lexer(source).tokenize();
				System.out.println("------- TOKENS -------");
				for (final Token token : tokens) System.out.println(token);
				System.out.println("------- END -------");
				return true;
			} catch (SyntaxError e) {
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean execute(String source, Interpreter interpreter, ExecutionOptions options) throws AbruptCompletion, CannotParse, SyntaxError {
		if (source.startsWith("// @opt: ")) {
			return handleOptions(source, options);
		}

		final Program program = parse(source);
		if (options.showAST()) {
			// FIXME: options.stream()?
			System.out.println("------- AST -------");
			program.dump(0);
			System.out.println("------- END -------");
		}

		final Value<?> lastValue = program.execute(interpreter);

		if (options.showLastValue()) {
			final StringRepresentation representation = new StringRepresentation();
			lastValue.represent(representation);
			System.out.println(representation);
		}

		return true;
	}

	public static boolean executeFile(Path path, Interpreter interpreter, ExecutionOptions options) throws AbruptCompletion, CannotParse, SyntaxError {
		String source;

		try {
			source = Files.readString(path, Charset.defaultCharset());
		} catch (IOException e) {
			handleError(true, e, System.out);
			return false;
		}

		return execute(source, interpreter, options);
	}

	public static boolean executeWithHandling(String source, Interpreter interpreter, ExecutionOptions options) {
		try {
			return execute(source, interpreter, options);
		} catch (AbruptCompletion | CannotParse | SyntaxError throwable) {
			handleError(options.showDebug(), throwable, System.out);
			return false;
		}
	}

	public static boolean executeFileWithHandling(Path path, Interpreter interpreter, ExecutionOptions options) {
		try {
			return executeFile(path, interpreter, options);
		} catch (AbruptCompletion | CannotParse | SyntaxError throwable) {
			handleError(options.showDebug(), throwable, System.out);
			return false;
		}
	}

	public static void repl(Interpreter interpreter, ExecutionOptions options) {
		if (options.showPrompt()) System.out.println("Starting REPL...");
		final Scanner scanner = new Scanner(System.in);
		do {
			try {
				if (options.showPrompt()) System.out.print("> ");
				final String next = scanner.nextLine();
				if (next.isBlank()) continue;

				if (next.equals(".exit")) {
					break;
				} else if (next.equals(".clear")) {
					System.out.print("\033[H\033[2J");
					System.out.flush();
				} else {
					executeWithHandling(next, interpreter, options);
				}
			} catch (NoSuchElementException e) {
				break;
			}
		} while (true);
	}
}