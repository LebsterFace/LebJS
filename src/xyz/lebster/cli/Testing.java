package xyz.lebster.cli;

import xyz.lebster.ScriptExecutor;
import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;

public final class Testing {
	private static final ByteArrayOutputStream passedOutput = new ByteArrayOutputStream();
	private static final PrintStream passedStream = new PrintStream(passedOutput);
	private static final ByteArrayOutputStream failedOutput = new ByteArrayOutputStream();
	private static final PrintStream failedStream = new PrintStream(failedOutput);
	private static int successfulTests = 0;
	private static int totalTests = 0;

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
				ScriptExecutor.error(throwable, failedStream, options.showStackTrace());
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