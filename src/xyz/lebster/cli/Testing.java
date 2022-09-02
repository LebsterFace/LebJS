package xyz.lebster.cli;

import xyz.lebster.Main;
import xyz.lebster.core.ANSI;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.exception.ParserNotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Realm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public final class Testing {
	private final ByteArrayOutputStream passedOutput;
	private final PrintStream passedStream;
	private final ByteArrayOutputStream failedOutput;
	private final PrintStream failedStream;
	private int successfulTests = 0;
	private int totalTests = 0;

	private final CLArguments arguments;

	public Testing(CLArguments arguments) {
		this.arguments = arguments;
		if (arguments.options().disableTestOutputBuffers()) {
			passedOutput = null;
			passedStream = System.out;
			failedOutput = null;
			failedStream = System.out;
		} else {
			passedOutput = new ByteArrayOutputStream();
			passedStream = new PrintStream(passedOutput);
			failedOutput = new ByteArrayOutputStream();
			failedStream = new PrintStream(failedOutput);
		}
	}

	private static void printTestResult(PrintStream stream, String color, String status, String name) {
		stream.printf("%s%s %s %s%s %s%s%n", ANSI.BACKGROUND_BLACK, color, status, ANSI.RESET, ANSI.BRIGHT_BLUE, name, ANSI.RESET);
	}

	private static void printTestOutput(PrintStream printStream, ByteArrayOutputStream baos) {
		if (!baos.toString().isBlank())
			printStream.println(baos);
	}

	private void runTestFile(File file, String prefix) {
		if (!file.getName().endsWith(".js") && !file.getName().endsWith(".js.skip")) return;

		final String fileName = prefix + file.getName();
		totalTests++;

		if (file.getName().endsWith(".js.skip")) {
			printTestResult(failedStream, ANSI.YELLOW, "SKIPPED", fileName);
			return;
		}

		final ByteArrayOutputStream tempOutput = new ByteArrayOutputStream();
		final PrintStream tempStream = new PrintStream(tempOutput);
		final PrintStream stdout = System.out;
		System.setOut(tempStream);

		try {
			try {
				Realm.executeStatic(Main.readFile(file.toPath()), arguments.options().showAST());
			} catch (AbruptCompletion exception) {
				if (!arguments.options().parseOnly()) {
					throw exception;
				}
			} catch (NotImplemented | ParserNotImplemented exception) {
				if (!arguments.options().ignoreNotImplemented()) {
					throw exception;
				}
			}
			successfulTests++;
			printTestResult(passedStream, ANSI.BRIGHT_GREEN, "PASSED", fileName);
			printTestOutput(passedStream, tempOutput);
		} catch (Throwable throwable) {
			printTestResult(failedStream, ANSI.BRIGHT_RED, "FAILED", fileName);
			printTestOutput(failedStream, tempOutput);
			Main.handleError(throwable, failedStream, arguments.options().hideStackTrace());
		}

		System.setOut(stdout);
	}

	private void runTestDirectory(File directory, String prefix) {
		final File[] files = directory.listFiles();
		if (files == null) throw new Error("Test directory not found!");

		for (final File file : files) {
			if (file.isFile()) {
				runTestFile(file, prefix);
			} else {
				runTestDirectory(file, prefix + file.getName() + "/");
			}
		}
	}

	public void test() {
		final File testingDirectory = arguments.filePathOrNull() == null ? new File("tests/") : arguments.filePathOrNull().toFile();
		runTestDirectory(testingDirectory, "");

		if (!arguments.options().disableTestOutputBuffers()) {
			passedStream.close();
			failedStream.close();
		}

		try {
			System.out.printf("%n%s%s\t\tPassing Tests (%d/%d)%n%s%n", ANSI.BACKGROUND_GREEN, ANSI.BLACK, successfulTests, totalTests, ANSI.RESET);
			if (!arguments.options().disableTestOutputBuffers() && !arguments.options().hidePassing())
				passedOutput.writeTo(System.out);

			System.out.printf("%n%s%s\t\tFailing Tests (%d/%d)%n%s%n", ANSI.BACKGROUND_RED, ANSI.BLACK, totalTests - successfulTests, totalTests, ANSI.RESET);
			if (!arguments.options().disableTestOutputBuffers())
				failedOutput.writeTo(System.out);
			if (totalTests != successfulTests)
				System.out.printf("%n%s\t\t%.2f%% of tests passed%n%s%n", ANSI.MAGENTA, 100.0D * ((double) successfulTests / (double) totalTests), ANSI.RESET);
		} catch (IOException e) {
			throw new Error(e.getMessage());
		}
	}
}