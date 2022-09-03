package xyz.lebster.cli;

import java.io.File;

interface TestHarness {
	TestResult run(File file, CLArguments arguments);
}
