package xyz.lebster;

public record CommandLineArgs(boolean showAST, String fileName, ExecutionMode mode) {
	static CommandLineArgs fromArgs(String[] args) {
		boolean showAST = false;
		ExecutionMode mode = ExecutionMode.Default;
		String fileName = null;

		for (final String arg : args) {
			if (arg.startsWith("-")) {
				if (arg.equalsIgnoreCase("-A") || arg.equalsIgnoreCase("-ast")) {
					showAST = true;
				} else if (arg.equalsIgnoreCase("-r") || arg.equalsIgnoreCase("-repl")) {
					mode = ExecutionMode.REPL;
				} else if (arg.equalsIgnoreCase("-f") || arg.equalsIgnoreCase("-file")) {
					mode = ExecutionMode.File;
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

		return new CommandLineArgs(showAST, fileName, mode);
	}
}
