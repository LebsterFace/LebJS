package xyz.lebster;

import xyz.lebster.exception.LanguageException;
import xyz.lebster.node.*;
import xyz.lebster.value.Number;
import xyz.lebster.value.Value;

public class Main {
	public static void main(String[] args) {
		Program program = new Program();

        FunctionDeclaration foo = new FunctionDeclaration("foo");
        foo.append(new ReturnStatement(
        	new BinaryExpression(
				new Number(3),
				new Number(6),
				BinaryOp.Add
			)
		));

        program.append(foo);
        program.append(new CallExpression("foo"));
		System.out.println("------- PROGRAM DUMP -------");
		program.dump(0);

		try {
			Interpreter interpreter = new Interpreter();
			final Value<?> result = program.execute(interpreter);
			System.out.println("------- RESULT -------");
			result.dump(0);
		} catch (LanguageException e) {
			System.out.println("------- ERROR -------");
			e.printStackTrace();
		}
	}
}
