package xyz.lebster.cli;

import xyz.lebster.ScriptExecutor;
import xyz.lebster.core.exception.UnexpectedEndOfInput;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.Program;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.parser.Lexer;
import xyz.lebster.core.parser.Parser;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class REPL {
	private final CLArguments.ExecutionOptions options;
	private final Scanner scanner = new Scanner(System.in);
	private final Interpreter interpreter = new Interpreter();

	private boolean isMultiline = false;
	private final ArrayList<String> multilineComponents = new ArrayList<>();

	public REPL(CLArguments.ExecutionOptions options) {
		this.options = options;
	}

	public void run() {
		System.out.println("Starting REPL...");
		do {
			this.prompt();
			try {
				final String nextLine = scanner.nextLine();
				if (nextLine.isBlank()) continue;

				if (nextLine.equals(".exit")) {
					break;
				} else if (nextLine.equals(".clear")) {
					System.out.print("\033[H\033[2J");
					System.out.flush();
				} else {
					this.handle(nextLine);
				}
			} catch (NoSuchElementException e) {
				break;
			}
		} while (true);
	}

	private void handle(String currentLine) {
		if (isMultiline) multilineComponents.add(currentLine);
		final Program program = getProgram(currentLine);
		if (program == null) return;
		else if (isMultiline) endMultiline();

		if (options.showAST()) {
			System.out.println("------- AST -------");
			program.dump(0);
			System.out.println("------- END -------");
		}

		try {
			final Value<?> lastValue = program.execute(interpreter);
			System.out.println(lastValue.toDisplayString());
		} catch (Throwable e) {
			ScriptExecutor.error(e, System.out, options.showStackTrace());
		}
	}

	private String getMultilineSource() {
		return String.join("\n", multilineComponents);
	}

	private Program getProgram(String currentLine) {
		try {
			return new Parser(new Lexer(isMultiline ? getMultilineSource() : currentLine).tokenize()).parse();
		} catch (Throwable e) {
			if (e instanceof UnexpectedEndOfInput) {
				if (!isMultiline) {
					isMultiline = true;
					multilineComponents.add(currentLine);
				}
			} else {
				endMultiline();
				ScriptExecutor.error(e, System.out, options.showStackTrace());
			}

			return null;
		}
	}

	private void endMultiline() {
		isMultiline = false;
		multilineComponents.clear();
	}

	private void prompt() {
		if (this.isMultiline)
			System.out.print("... ");
		else
			System.out.print("> ");
	}
}
