package xyz.lebster.cli;

import java.nio.file.Path;

public record CLArguments(Path filePathOrNull, ExecutionMode mode, ExecutionOptions options) {
	public static CLArguments from(String[] args) throws CLArgumentException {
		final TemporaryResult result = new TemporaryResult();

		for (final String argument : args) {
			if (argument.startsWith("-")) {
				if (argument.startsWith("--")) {
					result.setFlag(argument.substring(2).toLowerCase());
				} else {
					result.setFlags(argument.substring(1).toLowerCase());
				}
			} else {
				result.setFilename(argument);
			}
		}

		return result.toCLIArguments();
	}

	public record ExecutionOptions(
		boolean showAST,
		boolean hideStackTrace,
		boolean parseOnly,
		boolean ignoreNotImplemented,
		boolean hidePassing,
		boolean disableTestOutputBuffers
	) {
	}

	private static final class TemporaryResult {
		private String fileNameOrNull = null;
		private ExecutionMode mode = null;

		private boolean showAST = false;
		private boolean hideStackTrace = true;
		private boolean parseOnly = false;
		private boolean ignoreNotImplemented = false;
		private boolean hidePassing = false;
		private boolean disableTestOutputBuffers = false;

		private ExecutionOptions toExecutionOptions() {
			return new ExecutionOptions(
				this.showAST,
				this.hideStackTrace,
				this.parseOnly,
				this.ignoreNotImplemented,
				this.hidePassing,
				this.disableTestOutputBuffers
			);
		}

		private void setFlag(String flag) throws CLArgumentException {
			switch (flag) {
				case "a", "ast" -> showAST = true;
				case "s", "show", "stack", "v", "verbose" -> hideStackTrace = false;
				case "parse-only" -> parseOnly = true;
				case "ignore-not-impl" -> ignoreNotImplemented = true;
				case "hide-passing" -> hidePassing = true;
				case "no-buffer" -> disableTestOutputBuffers = true;

				case "t", "test" -> setMode(ExecutionMode.Tests);
				case "g", "gif" -> {
					setMode(ExecutionMode.GIF);
					showAST = true;
				}

				default -> throw new CLArgumentException("Unknown flag '" + flag + "'");
			}
		}

		private void setMode(ExecutionMode newMode) throws CLArgumentException {
			if (this.mode == null) {
				this.mode = newMode;
			} else {
				throw new CLArgumentException("Mode was specified twice");
			}
		}

		private void setFlags(String flags) throws CLArgumentException {
			for (int i = 0; i < flags.length(); i++) {
				this.setFlag(flags.substring(i, i + 1));
			}
		}

		private void setFilename(String argument) throws CLArgumentException {
			if (this.mode != null) {
				if (this.mode == ExecutionMode.File) {
					throw new CLArgumentException("File name was specified twice");
				} else if (this.mode == ExecutionMode.GIF) {
					throw new CLArgumentException("File name should not be specified for mode GIF");
				}
			}

			this.fileNameOrNull = argument;
			if (this.mode == null)
				this.mode = ExecutionMode.File;
		}

		private CLArguments toCLIArguments() {
			if (this.mode == null) {
				this.mode = ExecutionMode.REPL;
			}

			return new CLArguments(this.fileNameOrNull == null ? null : Path.of(this.fileNameOrNull), this.mode, this.toExecutionOptions());
		}
	}
}