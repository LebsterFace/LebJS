package xyz.lebster;

import xyz.lebster.exception.LanguageException;
import xyz.lebster.node.*;
import xyz.lebster.value.Number;
import xyz.lebster.value.Value;

public class Main {
	public static void main(String[] args) {
		Program program = new Program();

		Identifier varA = new Identifier("a");
		Identifier varB = new Identifier("b");
		Identifier varC = new Identifier("c");
		Identifier funcFoo = new Identifier("foo");

		VariableDeclaration innerDefs = new VariableDeclaration(
			new VariableDeclarator[] {
				new VariableDeclarator(varA, new Number(1)),
				new VariableDeclarator(varB, new Number(2))
			}
		);

		// function foo() {
        FunctionDeclaration foo = new FunctionDeclaration(funcFoo);
        // var a = 1, b = 2;
        foo.append(innerDefs);
        // return a + b + c;
        foo.append(new ReturnStatement(new BinaryExpression(varA, new BinaryExpression(varB, varC, BinaryOp.Add), BinaryOp.Add)));
        // }

		program.append(foo);

		// var c = 3;
        program.append(new VariableDeclaration(
			new VariableDeclarator[] {
				new VariableDeclarator(varC, new Number(3))
			}
		));

        // foo();
        program.append(new CallExpression(funcFoo));
		System.out.println("------- PROGRAM DUMP -------");
		program.dump(0);

		try {
			final Interpreter interpreter = new Interpreter(program);
			final Value<?> result = program.execute(interpreter);
			System.out.println("------- RESULT -------");
			result.dump(0);
		} catch (LanguageException e) {
			System.out.println("------- ERROR -------");
			e.printStackTrace();
		}
	}
}
