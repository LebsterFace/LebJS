package xyz.lebster.cli;

import xyz.lebster.Main;
import xyz.lebster.core.ANSI;
import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.Null;
import xyz.lebster.core.node.value.StringValue;
import xyz.lebster.core.node.value.Undefined;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.node.value.object.ObjectValue;
import xyz.lebster.core.runtime.ExecutionError;
import xyz.lebster.core.runtime.LanguageError;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import static xyz.lebster.cli.ScriptExecutor.executeFile;

public final class Testing {
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

			if (file.getName().endsWith(".js.skip")) {
				printTestResult(failedStream, ANSI.YELLOW, "SKIPPED", file.getName());
				continue;
			}

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

		passedStream.close();
		failedStream.close();

		try {
			System.out.printf("%n%s%s\t\tPassing Tests (%d/%d)%n%s%n", ANSI.BACKGROUND_GREEN, ANSI.BLACK, successfulTests, totalTests, ANSI.RESET);
			passedTests.writeTo(System.out);
			System.out.printf("%n%s%s\t\tFailing Tests (%d/%d)%n%s%n", ANSI.BACKGROUND_RED, ANSI.BLACK, totalTests - successfulTests, totalTests, ANSI.RESET);
			failedTests.writeTo(System.out);
			if (totalTests != successfulTests)
				System.out.printf("%n%s\t\t%.2f%% of tests passed%n%s%n", ANSI.MAGENTA, 100.0D * ((double) successfulTests / (double) totalTests), ANSI.RESET);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Test runTest(File file, ExecutionOptions options) {
		final var tempOutput = new ByteArrayOutputStream();
		final var tempStream = new PrintStream(tempOutput);
		System.setOut(tempStream);

		try {
			return new Test(executeFile(file.toPath(), new Interpreter(), options), null, tempOutput.toString());
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

	public static void addTestingMethods(ObjectValue globalObject) {
		globalObject.setMethod("expect", (interpreter, arguments) -> {
			final Value<?> expected = arguments[0];
			final Value<?> received = arguments[1];

			if (!expected.equals(received)) {
				Dumper.dumpIndicator(0, "Expected");
				Dumper.dumpValue(0, expected.type.name(), String.valueOf(expected.value));
				Dumper.dumpIndicator(0, "Received");
				Dumper.dumpValue(0, received.type.name(), String.valueOf(received.value));
				throw new ExecutionError("Assertion failed!");
			}

			return Undefined.instance;
		});

		globalObject.setMethod("bind", (interpreter, arguments) -> {
			// Exit the `bind()` ExecutionContext
			final ExecutionContext current = interpreter.getExecutionContext();
			interpreter.exitExecutionContext(current);
			// Enter the new ExecutionContext
			interpreter.enterExecutionContext(new ExecutionContext(interpreter.lexicalEnvironment(), null, arguments[0]));
			// Re-enter the `bind()` ExecutionContext
			interpreter.enterExecutionContext(current);
			return Undefined.instance;
		});

		globalObject.setMethod("unbind", (interpreter, arguments) -> {
			// Exit the `unbind()` ExecutionContext
			final ExecutionContext current = interpreter.getExecutionContext();
			interpreter.exitExecutionContext(current);
			// Exit the bound ExecutionContext
			interpreter.exitExecutionContext(interpreter.getExecutionContext());
			// Re-enter the `unbind()` ExecutionContext
			interpreter.enterExecutionContext(current);
			return Undefined.instance;
		});

		globalObject.setMethod("createObject", (interpreter, arguments) -> {
			final ObjectValue result = new ObjectValue();
			for (int i = 0; i < arguments.length; i += 2) {
				final StringValue key = arguments[i].getValue(interpreter).toStringValue(interpreter);
				if (arguments.length - 1 == i) {
					throw AbruptCompletion.error(new LanguageError("Unmatched key '" + key.value + "' in createObject"));
				}

				final Value<?> value = arguments[i + 1].getValue(interpreter);
				result.put(key, value);
			}

			return result;
		});

		globalObject.setMethod("__proto__", (interpreter, values) -> {
			if (values.length == 0) {
				throw AbruptCompletion.error(new LanguageError("You must provide an object to get the prototype of"));
			} else if (values.length > 1) {
				throw AbruptCompletion.error(new LanguageError("Multiple objects were provided"));
			} else {
				final var prototype = values[0].toObjectValue(interpreter).getPrototype();
				return prototype == null ? Null.instance : prototype;
			}
		});
	}

	private record Test(boolean passed, Throwable error, String output) {
	}
}