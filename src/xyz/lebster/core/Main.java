package xyz.lebster.core;

import xyz.lebster.core.exception.LanguageException;
import xyz.lebster.core.node.*;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.*;
import xyz.lebster.parser.Lexer;
import xyz.lebster.parser.Parser;
import xyz.lebster.parser.Token;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
	public static Program programOne() {
		return (Program) new Program()
		.append(new FunctionDeclaration(
			new Identifier("greet"),
			new Identifier[] {
				new Identifier("name")
			}
		).append(new CallExpression("print",
			new BinaryExpression(
				new StringLiteral("Hello "),
				new Identifier("name"),
				BinaryOp.Add
			)
		)))
		.append(new CallExpression(
			"greet",
			new StringLiteral("world")
		));
	}

	public static void execProgram(Program program, boolean showDebug) {
		final Dictionary globalObject = new Dictionary();

		globalObject.set("print", new NativeFunction((interpreter, values) -> {
			System.out.println(values[0].toStringLiteral());
			return new Undefined();
		}));

		globalObject.set("greet", new NativeFunction((interpreter, values) ->
			new StringLiteral("Hello, " + values[0].toStringLiteral().value)
		));

		if (showDebug) {
			System.out.println("------- PROGRAM DUMP -------");
			program.dump(0);
		}

		try {
			final Interpreter interpreter = new Interpreter(program, globalObject);
			if (showDebug) System.out.println("------- EXECUTION -------");
			final Value<?> result = program.execute(interpreter);
			if (showDebug) {
				System.out.println("------- LAST VALUE -------");
				result.dump(0);
			}

			if (showDebug) {
				System.out.println("------- VARIABLES -------");
				interpreter.dumpVariables();
			}

		} catch (LanguageException e) {
			e.printStackTrace();
		}

		if (showDebug) System.out.println("------- [[ END ]] -------");
	}

	public static void execProgram(String source, boolean showDebug) {
		final Token[] tokens = new Lexer(source).tokenize();
		if (showDebug) {
			for (Token token : tokens) System.out.println(token);
		}

		execProgram(new Parser(tokens).parse(), showDebug);
	}

	public static void main(String[] args) {
		String source;

		try {
			byte[] encoded = Files.readAllBytes(Path.of(args[0]));
			source = new String(encoded, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}


		execProgram(source, true);
	}
}
