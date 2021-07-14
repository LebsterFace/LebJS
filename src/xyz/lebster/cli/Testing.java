package xyz.lebster.cli;

import xyz.lebster.ANSI;
import xyz.lebster.Dumper;
import xyz.lebster.Main;
import xyz.lebster.interpreter.AbruptCompletion;
import xyz.lebster.interpreter.ExecutionContext;
import xyz.lebster.node.value.*;
import xyz.lebster.runtime.ExecutionError;
import xyz.lebster.runtime.LanguageError;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import static xyz.lebster.cli.ScriptExecutor.defaultGlobalObject;
import static xyz.lebster.cli.ScriptExecutor.executeFile;

public class Testing {
	public static void test(ExecutionOptions options) {
		final File[] files = new File("tests/").listFiles();
		if (files == null) throw new Error("Test directory not found!");
		int successfulTests = 0;
		int totalTests = 0;

		final var passedTests = new ByteArrayOutputStream();
		final var passedStream = new PrintStream(passedTests);
		final var failedTests = new ByteArrayOutputStream();
		final var failedStream = new PrintStream(failedTests);

		for (final File file : files) {
			if (!file.isFile()) continue;
			totalTests++;

			final Test result = runTest(file, options);
			if (result.passed()) {
				printTestResult(passedStream, ANSI.BRIGHT_GREEN, "PASSED", file.getName());
				printTestOutput(passedStream, result.output(), options.silent());
				successfulTests++;
			} else {
				printTestResult(failedStream, ANSI.BRIGHT_RED, "FAILED", file.getName());
				printTestOutput(failedStream, result.output(), options.silent());
				if (!options.silent()) {
					ScriptExecutor.handleError(options.showDebug(), result.error(), failedStream);
					failedStream.println();
				}
			}
		}

		final double percentage = 100.0D * ((double) successfulTests / (double) totalTests);
		passedStream.close();
		failedStream.close();

		try {
			System.out.printf("%n%s%s\t\tPassing Tests (%d/%d)%n%s%n", ANSI.BACKGROUND_GREEN, ANSI.BLACK, successfulTests, totalTests, ANSI.RESET);
			passedTests.writeTo(System.out);
			System.out.printf("%n%s%s\t\tFailing Tests (%d/%d)%n%s%n", ANSI.BACKGROUND_RED, ANSI.BLACK, totalTests - successfulTests, totalTests, ANSI.RESET);
			failedTests.writeTo(System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Test runTest(File file, ExecutionOptions options) {
		final var tempOutput = new ByteArrayOutputStream();
		final var tempStream = new PrintStream(tempOutput);
		System.setOut(tempStream);

		try {
			return new Test(executeFile(file.toPath(), defaultGlobalObject(true), options), null, tempOutput.toString());
		} catch (Throwable throwable) {
			return new Test(false, throwable, tempOutput.toString());
		} finally {
			System.setOut(Main.stdout);
		}
	}

	private static void printTestResult(PrintStream stream, String color, String msg, String filename) {
		stream.printf("%s%s %s %s%s %s%s%n", ANSI.BACKGROUND_BLACK, color, msg, ANSI.RESET, ANSI.BRIGHT_BLUE, filename, ANSI.RESET);
	}

	private static void printTestOutput(PrintStream stream, String output, boolean silent) {
		if (!silent && !output.isBlank()) stream.println(output);
	}

	public static Dictionary addTestingMethods(Dictionary globalObject) {
		globalObject.set("expect", new NativeFunction((interpreter, arguments) -> {
			final Value<?> expected = arguments[0];
			final Value<?> received = arguments[1];

			if (!expected.equals(received)) {
				Dumper.dumpIndicated(0, "Expected", expected);
				Dumper.dumpIndicated(0, "Received", received);
				throw new ExecutionError("Assertion failed!");
			}

			return new Undefined();
		}));

		globalObject.set("bind", new NativeFunction((interpreter, arguments) -> {
			// Exit the `bind()` ExecutionContext
			final ExecutionContext current = interpreter.getExecutionContext();
			interpreter.exitExecutionContext(current);
			// Enter the new ExecutionContext
			interpreter.enterExecutionContext(new ExecutionContext(interpreter.lexicalEnvironment(), null, arguments[0]));
			// Re-enter the `bind()` ExecutionContext
			interpreter.enterExecutionContext(current);
			return new Undefined();
		}));

		globalObject.set("unbind", new NativeFunction((interpreter, arguments) -> {
			// Exit the `unbind()` ExecutionContext
			final ExecutionContext current = interpreter.getExecutionContext();
			interpreter.exitExecutionContext(current);
			// Exit the bound ExecutionContext
			interpreter.exitExecutionContext(interpreter.getExecutionContext());
			// Re-enter the `unbind()` ExecutionContext
			interpreter.enterExecutionContext(current);
			return new Undefined();
		}));

		globalObject.set("createObject", new NativeFunction((interpreter, arguments) -> {
			final Dictionary result = new Dictionary();
			for (int i = 0; i < arguments.length; i += 2) {
				final StringLiteral key = arguments[i].execute(interpreter).toStringLiteral();
				if (arguments.length - 1 == i) {
					final var error = new LanguageError("Unmatched key '" + key.value + "' in createObject");
					throw new AbruptCompletion(error, AbruptCompletion.Type.Throw);
				}

				final Value<?> value = arguments[i + 1].execute(interpreter);
				result.set(key, value);
			}

			return result;
		}));

		globalObject.set("__proto__", new NativeFunction((interpreter, values) -> {
			if (values.length == 0) {
				throw new AbruptCompletion(new LanguageError("You must provide an object to get the prototype of"), AbruptCompletion.Type.Throw);
			} else if (values.length > 1) {
				throw new AbruptCompletion(new LanguageError("Multiple values were provided"), AbruptCompletion.Type.Throw);
			} else {
				return values[0].toDictionary().getPrototype();
			}
		}));

		return globalObject;
	}

	private static record Test(boolean passed, Throwable error, String output) {
	}
}