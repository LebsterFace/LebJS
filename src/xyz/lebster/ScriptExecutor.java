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
			return ScriptExecutor.executeWithHandling(source, globalObject, showAST, false);
		} catch (IOException e) {
			handleError(e);
			return false;
		}
	}

	public static boolean executeWithHandling(String source, Dictionary globalObject, boolean showAST, boolean showLastValue) {
		try {
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

	private static void handleError(Throwable e) {
		System.out.println(Main.ANSI_RED + e.getClass().getSimpleName() + ": " + e.getLocalizedMessage() + Main.ANSI_RESET);
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
