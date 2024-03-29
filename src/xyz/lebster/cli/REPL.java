package xyz.lebster.cli;

import xyz.lebster.Main;
import xyz.lebster.core.ANSI;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.parser.Lexer;
import xyz.lebster.core.parser.Parser;
import xyz.lebster.core.parser.Token;
import xyz.lebster.core.value.JSONDisplayer;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.util.Scanner;

public record REPL(CLArguments.ExecutionOptions options, Scanner scanner, Interpreter interpreter) {
	public REPL(CLArguments.ExecutionOptions options) {
		this(options, new Scanner(System.in), new Interpreter());
	}

	public void run() {
		System.out.println("Starting REPL...");
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
							""",
						ANSI.CYAN, ANSI.RESET,
						ANSI.CYAN, ANSI.RESET,
						ANSI.CYAN, ANSI.BRIGHT_GREEN, ANSI.RESET, ANSI.BRIGHT_GREEN, ANSI.RESET
					);
				} else if (input.equals(".clear")) {
					System.out.print("\033[H\033[2J");
					System.out.flush();
				} else if (input.startsWith(".inspect ")) {
					final Value<?> lastValue = Parser.parse(input.substring(".inspect ".length())).execute(interpreter);
					if (lastValue instanceof final ObjectValue obj) {
						final StringBuilder builder = new StringBuilder();
						JSONDisplayer.display(builder, obj, true);
						System.out.println(builder);
					} else {
						System.out.println(lastValue.toDisplayString(false));
					}
				} else {
					final Value<?> lastValue = Parser.parse(input).execute(interpreter);
					interpreter.globalObject.set(interpreter, new StringValue("$"), lastValue);
					interpreter.globalObject.set(interpreter, new StringValue("$" + result), lastValue);
					result += 1;
					System.out.println(lastValue.toDisplayString(false));
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
			if (!result.isEmpty()) result.append('\n');
			final String line = readLine(indent);
			if (line == null) return null;
			final Token[] tokens = Lexer.tokenize(line);
			for (final Token token : tokens) {
				switch (token.type()) {
					case LParen, LBrace, LBracket -> indent++;
					case RParen, RBrace, RBracket -> indent--;
				}
			}

			result.append(line);
		} while (indent > 0);

		return result.toString();
	}
}
