package xyz.lebster.core;

import xyz.lebster.core.exception.LanguageException;
import xyz.lebster.core.node.*;
import xyz.lebster.core.runtime.Interpreter;
import xyz.lebster.core.value.*;
import xyz.lebster.parser.Lexer;
import xyz.lebster.parser.Parser;
import xyz.lebster.parser.Token;

import java.io.IOException;
import java.net.IDN;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
	public static Program programOne() {
		final Program result = (Program) new Program()
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

		return result;
	}

	public static void execProgram(Program program, boolean showDebug) {
		final Dictionary globalObject = new Dictionary();

		globalObject.set("print", new NativeFunction((interpreter, values) -> {
			System.out.println(values[0].toStringLiteral());
			return new Undefined();
		}));

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

	public static void main(String[] args) {
		String source = null;
		try {
			byte[] encoded = encoded = Files.readAllBytes(Path.of(System.getProperty("user.dir"), "tests/print.js"));
			source = new String(encoded, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		execProgram(new Parser(source).parse(), false);
	}
}
