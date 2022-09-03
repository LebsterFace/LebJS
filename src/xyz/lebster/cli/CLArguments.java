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
				result.setFlags(argument.substring(1).toLowerCase().split(""));
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
		private Iterator<String> arguments;
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

		private String resolveFlagAlias(String shortFlag) {
			return switch (shortFlag) {
				case "a" -> "ast";
				case "v" -> "verbose";
				case "t" -> "test";
				default -> null;
			};
		}

		private void setFlag(String flag) throws CLArgumentException {
			switch (flag) {
				case "ast" -> showAST = true;
				case "verbose" -> hideStackTrace = false;
				case "parse-only" -> parseOnly = true;
				case "ignore-not-impl" -> ignoreNotImplemented = true;
				case "hide-passing" -> hidePassing = true;
				case "no-buffer" -> disableTestOutputBuffers = true;
				case "harness" -> testHarnessPath = getFlagValue("Missing harness filepath");

				case "test" -> setMode(ExecutionMode.Tests);
				case "gif" -> {
					setMode(ExecutionMode.GIF);
					showAST = true;
				}

				default -> throw new CLArgumentException("Unknown flag '--%s'".formatted(flag));
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

		private void setFlags(String[] flags) throws CLArgumentException {
			for (final String flag : flags) {
				final String longFlag = resolveFlagAlias(flag);
				if (longFlag == null) throw new CLArgumentException("Unknown flag alias '-%s'".formatted(flag));
				setFlag(longFlag);
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