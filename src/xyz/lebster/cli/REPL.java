package xyz.lebster.cli;

import xyz.lebster.ScriptExecutor;
import xyz.lebster.core.exception.CannotParse;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class REPL {
	private final CLArguments.ExecutionOptions options;
	private final Scanner scanner = new Scanner(System.in);
	private final Interpreter interpreter = new Interpreter();

	public REPL(CLArguments.ExecutionOptions options) {
		this.options = options;
	}

	public void run() {
		System.out.println("Starting REPL...");
		do {
			this.prompt();
			try {
				if (!this.handle(scanner.nextLine())) break;
			} catch (NoSuchElementException e) {
				break;
			}
		} while (true);
	}

	private boolean handle(String next) {
		if (next.isBlank()) {
			return true;
		} else if (next.equals(".exit")) {
			return false;
		} else if (next.equals(".clear")) {
			System.out.print("\033[H\033[2J");
			System.out.flush();
			return true;
		}

		try {
			ScriptExecutor.executeWithoutErrorHandling(next, interpreter, options);
		} catch (SyntaxError | CannotParse | AbruptCompletion e) {
			ScriptExecutor.error(e, System.out, options.showStackTrace());
		}

		return true;
	}

	private void prompt() {
		System.out.print("> ");
	}
}
