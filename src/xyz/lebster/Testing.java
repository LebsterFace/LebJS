package xyz.lebster;

import xyz.lebster.core.runtime.CallFrame;
import xyz.lebster.core.value.*;
import xyz.lebster.exception.LanguageError;

import java.io.File;

public class Testing {
	public static void test(boolean showAST) {
		final File[] files = new File("tests/").listFiles();
		if (files == null) throw new Error("Test directory not found!");
		int successfulTests = 0;
		int totalTests = 0;

		for (final File file : files) {
			if (!file.isFile()) continue;
			totalTests++;
			System.out.println(ANSI.GREEN + "Testing " + file.getName() + "..." + ANSI.RESET);
			final Dictionary globalObject = ScriptExecutor.getDefaultGlobalObject();
			addTestingMethods(globalObject);
			final boolean succeeded = ScriptExecutor.executeFileWithHandling(file.toPath(), globalObject, showAST);
			if (succeeded) {
				System.out.println(ANSI.GREEN + "Passed!" + ANSI.RESET);
				successfulTests++;
			}
		}

		final double percentage = 100.0D * ((double) successfulTests / (double) totalTests);
		System.out.println("--- TESTING FINISHED ---");
		System.out.printf("%d passed out of %d total (%.3f%%)%n", successfulTests, totalTests, percentage);
	}

	private static void addTestingMethods(Dictionary globalObject) {
		globalObject.set("expect", new NativeFunction((interpreter, arguments) -> {
			final Value<?> expected = arguments[0];
			final Value<?> received = arguments[1];

			if (expected.equals(received)) {
				return new BooleanLiteral(true);
			} else {
				System.out.print("Expecting: ");
				expected.dump(0);
				System.out.print("Received: ");
				received.dump(0);
				throw new LanguageError("Assertion failed!");
			}
		}));

		globalObject.set("bind", new NativeFunction((interpreter, arguments) -> {
			final CallFrame current = interpreter.getCallFrame();
			interpreter.exitCallFrame();
			interpreter.enterCallFrame(new CallFrame(null, arguments[0]));
			interpreter.enterCallFrame(current);
			return new Undefined();
		}));

		globalObject.set("unbind", new NativeFunction((interpreter, arguments) -> {
			final CallFrame current = interpreter.getCallFrame();
			interpreter.exitCallFrame();
			interpreter.exitCallFrame();
			interpreter.enterCallFrame(current);
			return new Undefined();
		}));
	}
}
