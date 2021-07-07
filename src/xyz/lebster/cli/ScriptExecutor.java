package xyz.lebster.cli;

import xyz.lebster.ANSI;
import xyz.lebster.exception.ParseException;
import xyz.lebster.interpreter.Interpreter;
import xyz.lebster.node.Program;
import xyz.lebster.node.value.*;
import xyz.lebster.parser.Lexer;
import xyz.lebster.parser.Parser;
import xyz.lebster.parser.Token;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class ScriptExecutor {
	public static Dictionary defaultGlobalObject(boolean testingMethods) {
		final Dictionary globalObject = new Dictionary();

		globalObject.set("print", new NativeFunction((interpreter, values) -> {
			for (Value<?> value : values) {
				final StringLiteral str = value.toStringLiteral(interpreter);
				System.out.println(str.value);
			}

			return new Undefined();
		}));

		globalObject.set("globalThis", globalObject);
		if (testingMethods) Testing.addTestingMethods(globalObject);

		return globalObject;
	}

	public static void handleError(boolean showDebug, Throwable e, PrintStream stream) {
		stream.print(ANSI.RED);
		stream.print(e.getClass().getSimpleName());
		stream.print(": ");
		stream.println(e.getLocalizedMessage());
		if (showDebug) e.printStackTrace(stream);
		stream.print(ANSI.RESET);
	}

	public static Program parse(String source) throws ParseException {
		return new Parser(new Lexer(source).tokenize()).parse();
	}

	private static boolean handleOptions(String source, ExecutionOptions executionOptions) {
		if (source.startsWith("// @opt: tokenize-only")) {
			try {
				new Lexer(source).tokenize();
				return true;
			} catch (ParseException e) {
				return false;
			}
		} else if (source.startsWith("// @opt: dump-tokens")) {
			try {
				final Token[] tokens = new Lexer(source).tokenize();
				System.out.println("------- TOKENS -------");
				for (final Token token : tokens) System.out.println(token);
				System.out.println("------- END -------");
				return true;
			} catch (ParseException e) {
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean execute(String source, Dictionary globalObject, ExecutionOptions options) throws Throwable {
		if (source.startsWith("// @opt: ")) {
			return handleOptions(source, options);
		}

		final Program program = parse(source);
		if (options.showAST()) {
//			FIXME: options.stream()?
			System.out.println("------- AST -------");
			program.dump(0);
			System.out.println("------- END -------");
		}

		final Value<?> lastValue = program.execute(new Interpreter(program, globalObject));

		if (options.showLastValue()) {
			System.out.print("Last Value: ");
			lastValue.dump(0);
		}

		return true;
	}

	public static boolean executeFile(Path path, Dictionary globalObject, ExecutionOptions options) throws Throwable {
		String source;

		try {
			source = Files.readString(path, Charset.defaultCharset());
		} catch (IOException e) {
			handleError(true, e, System.out);
			return false;
		}

		return execute(source, globalObject, options);
	}

	public static boolean executeWithHandling(String source, Dictionary globalObject, ExecutionOptions options) {
		try {
			return execute(source, globalObject, options);
		} catch (Throwable throwable) {
			handleError(options.showDebug(), throwable, System.out);
			return false;
		}
	}

	public static boolean executeFileWithHandling(Path path, Dictionary globalObject, ExecutionOptions options) {
		try {
			return executeFile(path, globalObject, options);
		} catch (Throwable throwable) {
			handleError(options.showDebug(), throwable, System.out);
			return false;
		}
	}

	public static void repl(Dictionary globalObject, ExecutionOptions options) {
		if (options.showPrompt()) System.out.println("Starting REPL...");
		final Scanner scanner = new Scanner(System.in);

		do {
			if (options.showPrompt()) System.out.print("> ");
			final String next = scanner.nextLine();
			if (next.isBlank()) continue;
			else if (next.equals(".exit")) break;
			executeWithHandling(next, globalObject, options);
			if (!options.showPrompt()) System.out.println("#END");
		} while (true);
	}
}