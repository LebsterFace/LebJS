package xyz.lebster.cli;

import xyz.lebster.Main;
import xyz.lebster.core.ANSI;
import xyz.lebster.core.exception.SyntaxError;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public final class Testing {
	private int total = 0;

	private int passed = 0;
	private final ByteArrayOutputStream passedOutput;
	private final PrintStream passedStream;

	private int skipped = 0;
	private final ByteArrayOutputStream skippedOutput;
	private final PrintStream skippedStream;

	private int failed = 0;
	private final ByteArrayOutputStream failedOutput;
	private final PrintStream failedStream;

	private final TestHarness harness;
	private final CLArguments arguments;

	public Testing(CLArguments arguments) throws SyntaxError, CLArgumentException {
		this.arguments = arguments;
		if (arguments.options().disableTestOutputBuffers()) {
			passedOutput = null;
			passedStream = System.out;
			skippedOutput = null;
			skippedStream = System.out;
			failedOutput = null;
			failedStream = System.out;
		} else {
			passedStream = new PrintStream(passedOutput = new ByteArrayOutputStream());
			skippedStream = new PrintStream(skippedOutput = new ByteArrayOutputStream());
			failedStream = new PrintStream(failedOutput = new ByteArrayOutputStream());
		}

		if (arguments.options().harness() == null) {
			harness = new DefaultTestHarness();
		} else if ("ladybird".equals(arguments.options().harness())) {
			harness = new LadybirdTestHarness(arguments);
		} else {
			throw new IllegalStateException("Unknown test harness name: " + arguments.options().harness());
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
		total++;

		final ByteArrayOutputStream tempOutput = new ByteArrayOutputStream();
		final PrintStream tempStream = new PrintStream(tempOutput);
		final PrintStream stdout = System.out;
		System.setOut(tempStream);

		final TestResult result = harness.run(file, arguments);
		switch (result.status()) {
			case SKIPPED -> {
				skipped++;
				printTestResult(skippedStream, ANSI.YELLOW, "SKIPPED", fileName);
			}

			case PASSED -> {
				passed++;
				printTestResult(passedStream, ANSI.BRIGHT_GREEN, "PASSED", fileName);
				printTestOutput(passedStream, tempOutput);
			}

			case FAILED -> {
				failed++;
				printTestResult(failedStream, ANSI.BRIGHT_RED, "FAILED", fileName);
				printTestOutput(failedStream, tempOutput);
				Main.handleError(result.cause(), failedStream, arguments.options().hideStackTrace());
			}
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
			System.out.printf("%n%s%s\t\tPassing Tests (%d/%d)%n%s%n", ANSI.BACKGROUND_GREEN, ANSI.BLACK, passed, total, ANSI.RESET);
			if (!arguments.options().disableTestOutputBuffers() && !arguments.options().hidePassing())
				passedOutput.writeTo(System.out);

			if (skipped != 0) {
				System.out.printf("%n%s%s\t\tSkipped Tests (%d/%d)%n%s%n", ANSI.BACKGROUND_YELLOW, ANSI.BLACK, skipped, total, ANSI.RESET);
				if (!arguments.options().disableTestOutputBuffers() && !arguments.options().hidePassing())
					skippedOutput.writeTo(System.out);
			}

			System.out.printf("%n%s%s\t\tFailing Tests (%d/%d)%n%s%n", ANSI.BACKGROUND_RED, ANSI.BLACK, failed, total, ANSI.RESET);
			if (!arguments.options().disableTestOutputBuffers())
				failedOutput.writeTo(System.out);

			if (total != passed)
				System.out.printf("%n%s\t\t%.2f%% of tests passed%n%s%n", ANSI.MAGENTA, 100.0D * ((double) passed / (double) total), ANSI.RESET);
		} catch (IOException e) {
			throw new Error(e.getMessage());
		}
	}
}