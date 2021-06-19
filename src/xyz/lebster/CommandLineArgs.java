package xyz.lebster;

public record CommandLineArgs(boolean showAST, String sourceCode, ExecutionMode mode) {
	static CommandLineArgs fromArgs(String[] args) {
		boolean showAST = false;
		int sourceStart = -1;
		ExecutionMode mode = ExecutionMode.Default;

		for (int i = 0; i < args.length; i++) {
			final String arg = args[i];
			if (arg.startsWith("-")) {
				if (arg.equalsIgnoreCase("-A") || arg.equalsIgnoreCase("-ast")) {
					showAST = true;
				} else if (arg.equalsIgnoreCase("-r") || arg.equalsIgnoreCase("-repl")) {
					mode = ExecutionMode.REPL;
				} else if (arg.equalsIgnoreCase("-f") || arg.equalsIgnoreCase("-file")) {
					mode = ExecutionMode.File;
				} else if (arg.equalsIgnoreCase("-s") || arg.equalsIgnoreCase("-script")) {
					mode = ExecutionMode.Script;
				}
			} else {
				sourceStart = i;
				break;
			}
		}

		if (mode == ExecutionMode.Default) {
			mode = sourceStart == -1 ? ExecutionMode.REPL : ExecutionMode.File;
		}

		if (sourceStart == -1) {
			switch (mode) {
				case File -> throw new CommandLineArgumentException("You must provide a filename.");
				case Script -> throw new CommandLineArgumentException("You must provide a script to execute.");
			}
		} else if (mode == ExecutionMode.REPL) {
			throw new CommandLineArgumentException("Wait for the REPL to start before running a script.");
		}

		String sourceCode = null;
		if (sourceStart != -1) {
			final StringBuilder builder = new StringBuilder(args[sourceStart]);
			for (int i = sourceStart + 1; i < args.length; i++) builder.append(args[i]);
			sourceCode = builder.toString();
		}

		return new CommandLineArgs(showAST, sourceCode, mode);
	}
}
