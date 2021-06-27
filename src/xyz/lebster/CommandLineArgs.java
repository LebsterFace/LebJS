package xyz.lebster;

public record CommandLineArgs(boolean showAST, boolean showDebug, String fileName, ExecutionMode mode) {
	static CommandLineArgs fromArgs(String[] args) {
		boolean showAST = false;
		boolean showDebug = true;
		ExecutionMode mode = ExecutionMode.Default;
		String fileName = null;

		for (final String arg : args) {
			if (arg.startsWith("-")) {
				switch (arg.toLowerCase()) {
					case "-a", "-ast", "-tree" -> showAST = true;
					case "-d", "-disp", "-display" -> showDebug = false;
					case "-r", "-repl" -> mode = ExecutionMode.REPL;
					case "-f", "-file" -> mode = ExecutionMode.File;
					case "-t", "-tests", "-test" -> mode = ExecutionMode.Tests;
				}
			} else {
				if (fileName == null) {
					fileName = arg;
				} else {
					throw new CommandLineArgumentException("Two source files!");
				}
			}
		}

		if (mode == ExecutionMode.Default) {
			mode = fileName == null ? ExecutionMode.REPL : ExecutionMode.File;
		}

		if (fileName == null) {
			if (mode == ExecutionMode.File) {
				throw new CommandLineArgumentException("You must provide a filename.");
			}
		} else if (mode == ExecutionMode.REPL) {
			throw new CommandLineArgumentException("Wait for the REPL to start before running a script.");
		}

		return new CommandLineArgs(showAST, showDebug, fileName, mode);
	}
}
