package xyz.lebster.cli;

import java.nio.file.Path;

public record CLArguments(Path filePathOrNull, ExecutionMode mode, ExecutionOptions options) {
	public record ExecutionOptions(
		boolean showAST,
		boolean showStackTrace,
		boolean showLastValue
	) {}

	private static class TemporaryResult {
		private String fileNameOrNull = null;
		private ExecutionMode mode = null;

		private boolean showAST = false;
		private boolean showStackTrace = true;
		private boolean showLastValue = false;

		private ExecutionOptions toExecutionOptions() {
			return new ExecutionOptions(
				this.showAST,
				this.showStackTrace,
				this.showLastValue
			);
		}

		private void setFlag(String flag) throws CLArgumentException {
			switch (flag) {
				case "a", "ast" -> showAST = true;
				case "h", "hide" -> showStackTrace = false;

				case "t", "test" -> mode = ExecutionMode.Tests;
				case "g", "gif" -> {
					mode = ExecutionMode.GIF;
					showAST = true;
				}

				default -> throw new CLArgumentException("Unknown flag '" + flag + "'");
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
				} else {
					throw new CLArgumentException("File name should not be specified for mode " + mode.name());
				}
			}

			this.fileNameOrNull = argument;
			this.mode = ExecutionMode.File;

		}

		private CLArguments toCLIArguments() {
			if (this.mode == null) {
				this.mode = ExecutionMode.REPL;
				this.showLastValue = true;
			}

			return new CLArguments(this.fileNameOrNull == null ? null : Path.of(this.fileNameOrNull), this.mode, this.toExecutionOptions());
		}
	}

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
}