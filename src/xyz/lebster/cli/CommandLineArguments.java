package xyz.lebster.cli;

public record CommandLineArguments(String fileName, ExecutionMode mode, ExecutionOptions options) {
	public static CommandLineArguments from(String[] args) throws IllegalArgumentException {
		boolean showAST = false;
		boolean showLastValue = false;
		boolean showDebug = true;
		boolean silent = false;
		boolean testingMethods = false;
		boolean showPrompt = true;

		ExecutionMode mode = ExecutionMode.Default;
		String fileName = null;

		for (String arg : args) {
			if (arg.startsWith("-")) {
				switch (arg.toLowerCase()) {
					case "-r", "-repl" -> mode = ExecutionMode.REPL;
					case "-f", "-file" -> mode = ExecutionMode.File;
					case "-t", "-tests", "-test" -> mode = ExecutionMode.Tests;
					case "-d", "-demo", "-g", "-gif" -> mode = ExecutionMode.Demo;
					case "-h", "-hide", "-display" -> showDebug = false;
					case "-s", "-silent", "-q" -> silent = true;
					case "-a", "-ast", "-tree" -> showAST = true;
					case "-e", "-exp", "-expect" -> testingMethods = true;
					case "-l", "-last", "-showLast", "-show" -> showLastValue = true;
					case "-p", "-prompt", "-noPrompt" -> showPrompt = false;
				}
			} else if (fileName == null) {
				fileName = arg;
			} else {
				throw new IllegalArgumentException("Please provide only one filename (remove '" + arg + "' or '" + fileName + "')");
			}
		}

		if (mode == ExecutionMode.Default) {
			mode = fileName == null ? ExecutionMode.REPL : ExecutionMode.File;
		}

		if (fileName == null) {
			if (mode == ExecutionMode.File) {
				throw new IllegalArgumentException("You must provide a filename");
			}
		} else if (mode == ExecutionMode.REPL || mode == ExecutionMode.Demo || mode == ExecutionMode.Tests) {
			throw new IllegalArgumentException("Filename was not expected given mode " + mode.name());
		}

		switch (mode) {
			case REPL -> {
				showLastValue = true;
				silent = false;
			}

			case Demo -> showDebug = false;
		}

		return new CommandLineArguments(fileName, mode, new ExecutionOptions(
			showAST, showLastValue, showDebug, silent, testingMethods, showPrompt
		));
	}
}