package xyz.lebster.cli;

import xyz.lebster.Main;
import xyz.lebster.core.ANSI;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Realm;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.parser.Lexer;
import xyz.lebster.core.parser.Token;
import xyz.lebster.core.value.JSONDisplayer;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.util.Scanner;

public final class REPL {
	private final CLArguments.ExecutionOptions options;
	private final Scanner scanner = new Scanner(System.in);
	private final Interpreter interpreter = new Interpreter();

	public REPL(CLArguments.ExecutionOptions options) {
		this.options = options;
	}

	public void run() {
		System.out.println("Starting REPL...");
		final Realm realm = new Realm(interpreter);
		int result = 0;

		while (true) {
			try {
				final String input = this.readNextInput();
				if (input == null || input.equals(".exit")) break;
				if (input.isBlank()) continue;
				if (input.equals(".help")) {
					System.out.printf("""
							%s.help%s                      Display this message
							%s.clear%s                     Clear the screen
							%s.inspect%s [expression]%s      Deep print the result of %s[expression]%s
							%s.dump%s [code]%s               Dump the parsed AST of %s[code]%s
							""",
						ANSI.CYAN, ANSI.RESET,
						ANSI.CYAN, ANSI.RESET,
						ANSI.CYAN, ANSI.BRIGHT_GREEN, ANSI.RESET, ANSI.BRIGHT_GREEN, ANSI.RESET,
						ANSI.CYAN, ANSI.BRIGHT_GREEN, ANSI.RESET, ANSI.BRIGHT_GREEN, ANSI.RESET
					);
				} else if (input.equals(".clear")) {
					System.out.print("\033[H\033[2J");
					System.out.flush();
				} else if (input.startsWith(".inspect ")) {
					final Value<?> lastValue = realm.execute(input.substring(".inspect ".length()), options.showAST());
					if (lastValue instanceof final ObjectValue obj) {
						final var representation = new StringRepresentation();
						JSONDisplayer.display(representation, obj, true);
						System.out.println(representation);
					} else {
						System.out.println(lastValue.toDisplayString());
					}
				} else if (input.startsWith(".dump ")) {
					Realm.parse(input.substring(".dump ".length()), true);
				} else {
					final Value<?> lastValue = realm.execute(input, options.showAST());
					interpreter.globalObject.set(interpreter, new StringValue("$"), lastValue);
					interpreter.globalObject.set(interpreter, new StringValue("$" + result), lastValue);
					result += 1;
					System.out.println(lastValue.toDisplayString());
				}
			} catch (Throwable e) {
				Main.handleError(e, System.out, options.hideStackTrace());
			}
		}
	}

	private String readLine(int indent) {
		if (indent == 0 && options.showPrompt()) {
			System.out.print("> ");
		} else {
			for (int i = 0; i < indent; i++)
				System.out.print('\t');
		}

		return this.scanner.hasNextLine() ? this.scanner.nextLine() : null;
	}

	private String readNextInput() throws SyntaxError {
		final StringBuilder result = new StringBuilder();
		int indent = 0;

		do {
			if (result.length() != 0) result.append('\n');
			final String line = readLine(indent);
			if (line == null) return null;
			final Token[] tokens = Lexer.tokenize(line);
			for (final Token token : tokens) {
				switch (token.type) {
					case LParen, LBrace, LBracket -> indent++;
					case RParen, RBrace, RBracket -> indent--;
				}
			}

			result.append(line);
		} while (indent > 0);

		return result.toString();
	}
}
