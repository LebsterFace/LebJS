package xyz.lebster.cli;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;

public record CLArguments(Path filePathOrNull, ExecutionMode mode, ExecutionOptions options) {
	public static CLArguments from(String[] args) throws CLArgumentException {
		final Iterator<String> iterator = Arrays.asList(args).iterator();
		final TemporaryResult result = new TemporaryResult(iterator);

		while (iterator.hasNext()) {
			final String argument = iterator.next();
			if (argument.startsWith("--")) {
				result.setFlag(argument.substring(2).toLowerCase());
			} else if (argument.startsWith("-")) {
				for (final String flag : argument.substring(1).toLowerCase().split("")) {
					result.setFlag(flag);
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
		boolean disableTestOutputBuffers,
		String testHarnessName
	) {
	}

	private static final class TemporaryResult {
		private final Iterator<String> arguments;
		public TemporaryResult(Iterator<String> arguments) {
			this.arguments = arguments;
		}

		private String fileNameOrNull = null;
		private ExecutionMode mode = null;

		private boolean showAST = false;
		private boolean hideStackTrace = true;
		private boolean parseOnly = false;
		private boolean ignoreNotImplemented = false;
		private boolean hidePassing = false;
		private boolean disableTestOutputBuffers = false;
		private String testHarnessPath;

		private ExecutionOptions toExecutionOptions() {
			return new ExecutionOptions(
				this.showAST,
				this.hideStackTrace,
				this.parseOnly,
				this.ignoreNotImplemented,
				this.hidePassing,
				this.disableTestOutputBuffers,
				this.testHarnessPath
			);
		}

		private void setFlag(String flag) throws CLArgumentException {
			switch (flag) {
				case "a", "ast" -> showAST = true;
				case "v", "verbose" -> hideStackTrace = false;
				case "parse-only" -> parseOnly = true;
				case "ignore-not-impl" -> ignoreNotImplemented = true;
				case "hide-passing" -> hidePassing = true;
				case "no-buffer" -> disableTestOutputBuffers = true;
				case "h", "harness" -> testHarnessPath = getFlagValue("Missing harness filepath");

				case "t", "test" -> setMode(ExecutionMode.Tests);
				case "gif" -> {
					setMode(ExecutionMode.GIF);
					showAST = true;
				}

				default -> throw new CLArgumentException("Unknown flag '%s'".formatted(flag));
			}
		}

		private String getFlagValue(String missingMessage) throws CLArgumentException {
			if (!arguments.hasNext()) throw new CLArgumentException(missingMessage);
			return arguments.next();
		}

		private void setMode(ExecutionMode newMode) throws CLArgumentException {
			if (this.mode == null) {
				this.mode = newMode;
			} else {
				throw new CLArgumentException("Mode was specified twice");
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
			return new CLArguments(
				fileNameOrNull == null ? null : Path.of(fileNameOrNull),
				mode == null ? ExecutionMode.REPL : mode,
				toExecutionOptions()
			);
		}
	}
}