package xyz.lebster.core.interpreter;

import xyz.lebster.core.exception.CannotParse;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.node.Program;
import xyz.lebster.core.parser.Lexer;
import xyz.lebster.core.parser.Parser;
import xyz.lebster.core.runtime.value.Value;

public record Realm(Interpreter interpreter) {
	public static void executeStatic(String sourceText, boolean dumpAST) throws CannotParse, AbruptCompletion, SyntaxError {
		new Realm(new Interpreter()).execute(sourceText, dumpAST);
	}

	public Program parse(String sourceText) throws SyntaxError, CannotParse {
		return new Parser(new Lexer(sourceText).tokenize()).parse();
	}

	public Value<?> execute(String sourceText, boolean dumpAST) throws CannotParse, SyntaxError, AbruptCompletion {
		final Program program = parse(sourceText);
		if (dumpAST) {
			System.out.println("------- AST -------");
			program.dump(0);
			System.out.println("------- END -------");
		}
		return program.execute(interpreter);
	}
}
