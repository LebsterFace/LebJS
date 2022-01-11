package xyz.lebster.cli;

import xyz.lebster.ScriptExecutor;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.Program;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.parser.Lexer;
import xyz.lebster.core.parser.Parser;

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
		while (true) {
			this.prompt();
			final String input = this.readNextInput();
			if (input == null || input.equals(".exit")) break;
			if (input.isBlank()) continue;

			if (input.equals(".clear")) {
				System.out.print("\033[H\033[2J");
				System.out.flush();
			} else {
				try {
					final Lexer lexer = new Lexer(input);
					Program program = new Parser(lexer.tokenize()).parse();
					if (options.showAST()) {
						System.out.println("------- AST -------");
						program.dump(0);
						System.out.println("------- END -------");
					}

					final Value<?> lastValue = program.execute(interpreter);
					System.out.println(lastValue.toDisplayString());
				} catch (Throwable e) {
					ScriptExecutor.error(e, System.out, options.showStackTrace());
				}
			}
		}
	}

	private String readNextInput() {
		return this.scanner.hasNextLine() ? scanner.nextLine() : null;
	}

	private void prompt() {
		System.out.print("> ");
	}
}
