package xyz.lebster.cli;

import xyz.lebster.Main;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Realm;
import xyz.lebster.core.parser.Lexer;
import xyz.lebster.core.parser.Token;
import xyz.lebster.core.runtime.value.Value;

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

		while (true) {
			try {
				final String input = this.readNextInput();
				if (input == null || input.equals(".exit")) break;
				if (input.isBlank()) continue;
				if (input.equals(".clear")) {
					System.out.print("\033[H\033[2J");
					System.out.flush();
					continue;
				}

				final Value<?> lastValue = realm.execute(input, options.showAST());
				System.out.println(lastValue.toDisplayString());
			} catch (Throwable e) {
				Main.handleError(e, System.out, options.hideStackTrace());
			}
		}
	}

	private String readLine(int indent) {
		if (!options.hidePrompt()) {
			if (indent == 0) {
				System.out.print("> ");
			} else {
				for (int i = 0; i < indent; i++)
					System.out.print('\t');
			}
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
			final Token[] tokens = new Lexer(line).tokenize();
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
