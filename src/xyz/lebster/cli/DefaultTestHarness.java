package xyz.lebster.cli;

import xyz.lebster.Main;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.exception.ParserNotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Realm;

import java.io.File;

import static xyz.lebster.cli.TestStatus.*;

record DefaultTestHarness() implements TestHarness {
	@Override
	public TestResult run(File file, CLArguments arguments) {
		if (file.getName().endsWith(".js.skip"))
			return new TestResult(SKIPPED, null);

		try {
			Realm.executeStatic(Main.readFile(file.toPath()), arguments.options().showAST());
		} catch (AbruptCompletion exception) {
			if (!arguments.options().parseOnly())
				return new TestResult(FAILED, exception);
		} catch (NotImplemented | ParserNotImplemented exception) {
			if (!arguments.options().ignoreNotImplemented())
				return new TestResult(FAILED, exception);
		} catch (Throwable throwable) {
			return new TestResult(FAILED, throwable);
		}

		return new TestResult(PASSED, null);
	}
}
