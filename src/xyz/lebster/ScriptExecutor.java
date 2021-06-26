package xyz.lebster;

import xyz.lebster.core.node.Program;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.Dictionary;
import xyz.lebster.core.value.NativeFunction;
import xyz.lebster.core.value.Undefined;
import xyz.lebster.core.value.Value;
import xyz.lebster.exception.LanguageException;
import xyz.lebster.exception.ParseException;
import xyz.lebster.parser.Lexer;
import xyz.lebster.parser.Parser;
import xyz.lebster.parser.Token;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.stream.Stream;

public class ScriptExecutor {
	public static Dictionary getDefaultGlobalObject() {
		final Dictionary globalObject = new Dictionary();

		globalObject.set("print", new NativeFunction((interpreter, values) -> {
			final String[] strings = Stream.of(values).map(value -> value.toStringLiteral().value).toArray(String[]::new);
			final String joined = String.join(" ", strings);
			System.out.println(joined);
			return new Undefined();
		}));

		globalObject.set("globalThis", globalObject);
		return globalObject;
	}


	public static void repl(boolean showAST) {
		System.out.println("Starting REPL...");
		final Dictionary globalObject = getDefaultGlobalObject();
		final Scanner scanner = new Scanner(System.in);

		while (true) {
			System.out.print("> ");
			final String next = scanner.nextLine();
			if (next.isBlank()) continue;
			else if (next.equals(".exit")) break;
			executeWithHandling(next, globalObject, showAST, true);
		}
	}

	public static boolean executeFileWithHandling(Path path, Dictionary globalObject, boolean showAST) {
		try {
			final String source = Files.readString(path, Charset.defaultCharset());
			return executeWithHandling(source, globalObject, showAST, false);

		} catch (IOException e) {
			handleError(e);
			return false;
		}
	}

	public static boolean executeWithHandling(String source, Dictionary globalObject, boolean showAST, boolean showLastValue) {
		try {
			if (source.startsWith("// @opt: ")) {
				return handleOptions(source, globalObject, showAST, showLastValue);
			}

			final Value<?> lastValue = execute(parse(source), globalObject, showAST);
			if (showLastValue) {
				System.out.print("Last Value: ");
				lastValue.dump(0);
			}

			return true;
		} catch (ParseException | LanguageException e) {
			handleError(e);
			return false;
		}
	}

	private static boolean handleOptions(String source, Dictionary globalObject, boolean showAST, boolean showLastValue) {
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

	private static void handleError(Throwable e) {
		System.out.println(ANSI.RED + e.getClass().getSimpleName() + ": " + e.getLocalizedMessage());
		e.printStackTrace(System.out);
		System.out.print(ANSI.RESET);
	}

	public static Program parse(String source) throws ParseException {
		return new Parser(new Lexer(source).tokenize()).parse();
	}

	public static Value<?> execute(Program program, Dictionary globalObject, boolean showAST) throws LanguageException {
		if (showAST) {
			System.out.println("------- AST -------");
			program.dump(0);
			System.out.println("------- END -------");
		}

		return program.execute(new Interpreter(program, globalObject));
	}
}
