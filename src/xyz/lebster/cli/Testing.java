package xyz.lebster.cli;

import xyz.lebster.ScriptExecutor;
import xyz.lebster.core.ANSI;
import xyz.lebster.core.Dumper;
import xyz.lebster.core.interpreter.ExecutionContext;
import xyz.lebster.core.interpreter.GlobalObject;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.value.UndefinedValue;
import xyz.lebster.core.node.value.Value;
import xyz.lebster.core.runtime.error.ExecutionError;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;

public class Testing {
	private static final ByteArrayOutputStream passedOutput = new ByteArrayOutputStream();
	private static final PrintStream passedStream = new PrintStream(passedOutput);
	private static final ByteArrayOutputStream failedOutput = new ByteArrayOutputStream();
	private static final PrintStream failedStream = new PrintStream(failedOutput);
	private static int successfulTests = 0;
	private static int totalTests = 0;

	public static void addTestingMethods(GlobalObject globalObject) {
		globalObject.setMethod("expect", (interpreter, arguments) -> {
			final Value<?> expected = arguments[0];
			final Value<?> received = arguments[1];

			if (!expected.equals(received)) {
				Dumper.dumpIndicator(0, "Expected");
				Dumper.dumpValue(0, expected.type.name(), String.valueOf(expected.value));
				Dumper.dumpIndicator(0, "Received");
				Dumper.dumpValue(0, received.type.name(), String.valueOf(received.value));
				throw new ExecutionError("Assertion failed.");
			}

			return UndefinedValue.instance;
		});

		globalObject.setMethod("bind", (interpreter, arguments) -> {
			// Exit the `bind()` ExecutionContext
			final ExecutionContext current = interpreter.getExecutionContext();
			interpreter.exitExecutionContext(current);
			// Enter the new ExecutionContext
			interpreter.enterExecutionContext(new ExecutionContext(interpreter.lexicalEnvironment(), null, arguments[0]));
			// Re-enter the `bind()` ExecutionContext
			interpreter.enterExecutionContext(current);
			return UndefinedValue.instance;
		});

		globalObject.setMethod("unbind", (interpreter, arguments) -> {
			// Exit the `unbind()` ExecutionContext
			final ExecutionContext current = interpreter.getExecutionContext();
			interpreter.exitExecutionContext(current);
			// Exit the bound ExecutionContext
			interpreter.exitExecutionContext(interpreter.getExecutionContext());
			// Re-enter the `unbind()` ExecutionContext
			interpreter.enterExecutionContext(current);
			return UndefinedValue.instance;
		});
	}

	private static void printTestResult(PrintStream stream, String color, String status, String name) {
		stream.printf("%s%s %s %s%s %s%s%n", ANSI.BACKGROUND_BLACK, color, status, ANSI.RESET, ANSI.BRIGHT_BLUE, name, ANSI.RESET);
	}

	private static void printTestOutput(PrintStream printStream, ByteArrayOutputStream baos) {
		if (!baos.toString().isBlank())
			printStream.println(baos);
	}

	public static void test(CLArguments.ExecutionOptions options) {
		final File[] files = new File("tests/").listFiles();
		if (files == null) throw new Error("Test directory not found!");

		for (final File file : files) {
			if (!file.isFile()) continue;
			totalTests++;

			if (file.getName().endsWith(".js.skip")) {
				printTestResult(failedStream, ANSI.YELLOW, "SKIPPED", file.getName());
				continue;
			}

			final ByteArrayOutputStream tempOutput = new ByteArrayOutputStream();
			final PrintStream tempStream = new PrintStream(tempOutput);
			final PrintStream stdout = System.out;
			System.setOut(tempStream);

			try {
				ScriptExecutor.executeWithoutErrorHandling(Files.readString(file.toPath()), new Interpreter(), options);
				successfulTests++;
				printTestResult(passedStream, ANSI.BRIGHT_GREEN, "PASSED", file.getName());
				printTestOutput(passedStream, tempOutput);
			} catch (Throwable throwable) {
				printTestResult(failedStream, ANSI.BRIGHT_RED, "FAILED", file.getName());
				printTestOutput(failedStream, tempOutput);
				ScriptExecutor.error(throwable, failedStream, true);
			}

			System.setOut(stdout);
		}

		passedStream.close();
		failedStream.close();

		try {
			System.out.printf("%n%s%s\t\tPassing Tests (%d/%d)%n%s%n", ANSI.BACKGROUND_GREEN, ANSI.BLACK, successfulTests, totalTests, ANSI.RESET);
			passedOutput.writeTo(System.out);
			System.out.printf("%n%s%s\t\tFailing Tests (%d/%d)%n%s%n", ANSI.BACKGROUND_RED, ANSI.BLACK, totalTests - successfulTests, totalTests, ANSI.RESET);
			failedOutput.writeTo(System.out);
			if (totalTests != successfulTests)
				System.out.printf("%n%s\t\t%.2f%% of tests passed%n%s%n", ANSI.MAGENTA, 100.0D * ((double) successfulTests / (double) totalTests), ANSI.RESET);
		} catch (IOException e) {
			throw new Error(e.getMessage());
		}
	}
}