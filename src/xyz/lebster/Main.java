package xyz.lebster;

import xyz.lebster.exception.LanguageException;
import xyz.lebster.core.node.*;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.*;
import xyz.lebster.exception.ParseError;
import xyz.lebster.parser.Lexer;
import xyz.lebster.parser.Parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {
	public static final Dictionary globalObject = new Dictionary();
	static {
		globalObject.set("print", new NativeFunction((interpreter, values) -> {
			System.out.println(values[0].toStringLiteral());
			return new Undefined();
		}));

		globalObject.set("greet", new NativeFunction((interpreter, values) ->
			new StringLiteral("Hello, " + values[0].toStringLiteral().value)
		));
	}

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";

	public static void main(String[] args) {
		final CommandLineArgs CLArgs = CommandLineArgs.fromArgs(args);
		if (CLArgs.mode() == ExecutionMode.REPL) {
			repl(CLArgs.showAST());
		} else if (CLArgs.mode() == ExecutionMode.File) {
			try {
				final String source = new String(Files.readAllBytes(Path.of(CLArgs.fileName())), Charset.defaultCharset());
				executeWithHandling(source, CLArgs.showAST());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new CommandLineArgumentException("Mode could not be inferred");
		}
	}

	public static void executeWithHandling(String source, boolean showAST) {
		try {
			final Value<?> lastValue = execute(parse(source), showAST);
			System.out.print("Last Value: ");
			lastValue.dump(0);
		} catch (ParseError | LanguageException e) {
			System.err.println(ANSI_RED + e.getClass().getSimpleName() + ": " + e.getLocalizedMessage() + ANSI_RESET);
		}
	}

	public static void repl(boolean showAST) {
		System.out.println("Starting REPL...");
		final Scanner scanner = new Scanner(System.in);

		while (true) {
			System.out.print("> ");
			final String next = scanner.nextLine();
			if (next.isBlank()) continue;
			else if (next.equals(".exit")) break;
			executeWithHandling(next, showAST);
		}
	}

	public static Program parse(String source) throws ParseError {
		return new Parser(new Lexer(source).tokenize()).parse();
	}

	public static Value<?> execute(Program program, boolean showAST) throws LanguageException {
		if (showAST) {
			System.out.println("------- AST -------");
			program.dump(0);
			System.out.println("------- END -------");
		}

		return program.execute(new Interpreter(program, globalObject));
	}
}
