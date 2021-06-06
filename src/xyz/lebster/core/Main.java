package xyz.lebster.core;

import xyz.lebster.core.exception.LanguageException;
import xyz.lebster.core.node.*;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.*;

public class Main {
	public static void main(String[] args) {
		Program program = new Program();

		program.append(new VariableDeclaration(
			new VariableDeclarator[]{
				new VariableDeclarator(
					new Identifier("$message"),
					new StringLiteral("Hello world!")
				)
			}
		)).append(new CallExpression(
			new Identifier("print")
		));

		final Dictionary globalObject = new Dictionary();
		globalObject.set("print", new NativeFunction((interpreter, values) -> {
			System.out.println(interpreter.getVariable("$message").toStringLiteral());
			return new Undefined();
		}));

		System.out.println("------- PROGRAM DUMP -------");
		program.dump(0);

		try {
			final Interpreter interpreter = new Interpreter(program, globalObject);
			System.out.println("------- EXECUTION -------");
			final Value<?> result = program.execute(interpreter);
			System.out.println("------- LAST VALUE -------");
			result.dump(0);
			System.out.println("------- VARIABLES -------");
			interpreter.dumpVariables();
		} catch (LanguageException e) {
			System.out.println("------- ERROR -------");
			e.printStackTrace();
		}
		System.out.println("------- [[ END ]] -------");
	}
}
