package xyz.lebster.cli;

import xyz.lebster.Main;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.exception.ParserNotImplemented;
import xyz.lebster.core.exception.ShouldNotHappen;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Realm;
import xyz.lebster.core.node.Program;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.object.DataDescriptor;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.io.File;
import java.nio.file.Path;

import static xyz.lebster.cli.TestStatus.*;

final class SerenityTestHarness implements TestHarness {
	private final Program testCommon;
	private final Path commonPath;

	SerenityTestHarness(CLArguments arguments) throws SyntaxError, CLArgumentException {
		final Path path = arguments.filePathOrNull();
		if (path == null) throw new CLArgumentException("Test path is required for Serenity test harness");
		this.commonPath = path.resolve("test-common.js");
		this.testCommon = Realm.parse(Main.readFile(commonPath), arguments.options().showAST());
	}

	private ObjectValue getTestResults(Interpreter interpreter) throws AbruptCompletion {
		final Value<?> testResultsValue = interpreter.getBinding(Names.__TestResults__).getValue(interpreter);
		if (!(testResultsValue instanceof final ObjectValue testResults)) {
			throw new ShouldNotHappen("__TestResults__ was not an object after test");
		}

		return testResults;
	}

	@Override
	public TestResult run(File file, CLArguments arguments) {
		if (file.toPath().equals(commonPath))
			return new TestResult(SKIPPED, null);

		try {
			final Interpreter interpreter = new Interpreter();
			try {
				testCommon.execute(interpreter);
			} catch (AbruptCompletion e) {
				throw new RuntimeException(e);
			}

			final Program program = Realm.parse(Main.readFile(file.toPath()), arguments.options().showAST());
			program.execute(interpreter);

			for (final var entry : getTestResults(interpreter).value.entrySet()) {
				if (!(entry.getKey() instanceof StringValue)) throw new ShouldNotHappen("Key of __TestResults__ was not string");
				if (!(entry.getValue() instanceof final DataDescriptor suiteValueProperty)) throw new ShouldNotHappen("Property of __TestResults__ was not data descriptor");
				if (!(suiteValueProperty.value() instanceof final ObjectValue suiteValue)) throw new ShouldNotHappen("Suite value was not object");
				for (final var suiteEntry : suiteValue.value.entrySet()) {
					if (!(suiteEntry.getKey() instanceof StringValue)) throw new ShouldNotHappen("Test name was not string");
					if (!(suiteEntry.getValue() instanceof final DataDescriptor testValueProperty)) throw new ShouldNotHappen("Property of test suite was not data descriptor");
					if (!(testValueProperty.value() instanceof final ObjectValue testValue)) throw new ShouldNotHappen("Test value was not object");
					if (!testValue.value.containsKey(Names.result)) throw new ShouldNotHappen("Test value did not contain 'result' property");
					if (!(testValue.value.get(Names.result) instanceof final DataDescriptor testResultProperty)) throw new ShouldNotHappen("Test result was not data descriptor");
					if (!(testResultProperty.value() instanceof final StringValue testResult)) throw new ShouldNotHappen("Test result was not a string");

					// TODO: Count suites failed / passed
					final String resultString = testResult.value;
					if (resultString.equals("pass")) {
						// yay
					} else if (resultString.equals("fail")) {
						if (!(testValue.value.get(Names.details) instanceof final DataDescriptor testDetailsProperty)) throw new ShouldNotHappen("Test details property was not data descriptor");
						if (!(testDetailsProperty.value() instanceof final StringValue testDetails)) throw new ShouldNotHappen("Test details property was not a string");
						return new TestResult(FAILED, new ShouldNotHappen(testDetails.value));
					} else {
						// skip
					}
				}
			}

			return new TestResult(PASSED, null);
		} catch (AbruptCompletion exception) {
			if (!arguments.options().parseOnly()) return new TestResult(FAILED, exception);
		} catch (NotImplemented | ParserNotImplemented exception) {
			if (!arguments.options().ignoreNotImplemented()) return new TestResult(FAILED, exception);
		} catch (Throwable throwable) {
			return new TestResult(FAILED, throwable);
		}

		return new TestResult(PASSED, null);
	}
}
