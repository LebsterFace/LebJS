package xyz.lebster;

import xyz.lebster.exception.LanguageError;
import xyz.lebster.exception.NotImplemented;
import xyz.lebster.exception.ParseError;
import xyz.lebster.core.node.*;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.*;
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

	public static void main(String[] args) {
		final CommandLineArgs CLArgs = CommandLineArgs.fromArgs(args);
		try {
			switch (CLArgs.mode()) {
				case REPL -> repl(CLArgs.showAST());
				case File -> execute(parseFile(CLArgs.sourceCode()), CLArgs.showAST());
				case Script -> execute(parse(CLArgs.sourceCode()), CLArgs.showAST()).dump(0);
			}
		} catch (LanguageError e) {

		} catch (IOException e) {

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
			try {
				execute(parse(next), showAST).dump(0);
			} catch (ParseError e) {
				System.err.println("\u001B[31mFailed to parse.\n" + e.getClass().getSimpleName() + ": " + e.getLocalizedMessage());
			} catch (LanguageError e) {
				System.err.println(e.getClass().getSimpleName() + ": " + e.getLocalizedMessage());
			}
		}
	}

	public static Program parseFile(String filename) throws IOException, NotImplemented, ParseError {
		final String source = new String(Files.readAllBytes(Path.of(filename)), Charset.defaultCharset());
		return parse(source);
	}

	public static Program parse(String source) throws ParseError, NotImplemented {
		return new Parser(new Lexer(source).tokenize()).parse();
	}

	public static Value<?> execute(Program program, boolean showAST) throws LanguageError {
		if (showAST) {
			program.dump(0);
			System.out.println("------- EXECUTION -------");
		}

		return program.execute(new Interpreter(program, globalObject));
	}
}
