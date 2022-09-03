package xyz.lebster.core.interpreter;

import xyz.lebster.core.exception.CannotParse;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.node.Program;
import xyz.lebster.core.parser.Parser;
import xyz.lebster.core.value.Value;

public record Realm(Interpreter interpreter) {
	public static void executeStatic(String sourceText, boolean dumpAST) throws CannotParse, AbruptCompletion, SyntaxError {
		new Realm(new Interpreter()).execute(sourceText, dumpAST);
	}

	public static Value<?> executeWith(String sourceText, Interpreter interpreter) throws CannotParse, AbruptCompletion, SyntaxError {
		return parse(sourceText, false).execute(interpreter);
	}

	public static Program parse(String sourceText, boolean dumpAST) throws SyntaxError, CannotParse {
		final Program program = new Parser(sourceText).parse();
		if (dumpAST) {
			program.dump(0);
			System.out.println();
		}

		return program;

	}

	public Value<?> execute(String sourceText, boolean dumpAST) throws CannotParse, SyntaxError, AbruptCompletion {
		final Program program = parse(sourceText, dumpAST);
		return program.execute(interpreter);
	}
}
