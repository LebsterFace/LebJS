package xyz.lebster.core;

import xyz.lebster.core.exception.LanguageException;
import xyz.lebster.core.node.*;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.StringLiteral;
import xyz.lebster.core.value.Value;

public class Main {
	public static void main(String[] args) {
		Program program = new Program();

		program.append(new MemberExpression(
			new StringLiteral("foo"),
			new Identifier("length")
		));

		System.out.println("------- PROGRAM DUMP -------");
		program.dump(0);

		try {
			final Interpreter interpreter = new Interpreter(program);
			final Value<?> result = program.execute(interpreter);
			System.out.println("------- RESULT -------");
			result.dump(0);
			System.out.println("------- VARIABLES -------");
			interpreter.dumpVariables();
		} catch (LanguageException e) {
			System.out.println("------- ERROR -------");
			e.printStackTrace();
		}
	}
}
