package xyz.lebster;

import java.nio.file.Path;

public class Main {
	public static void main(String[] args) {
		final CommandLineArgs CLArgs = CommandLineArgs.fromArgs(args);

		switch (CLArgs.mode()) {
			case File -> ScriptExecutor.executeFileWithHandling(Path.of(CLArgs.fileName()), ScriptExecutor.getDefaultGlobalObject(), CLArgs.showAST());
			case Tests -> Testing.test(CLArgs.showAST());
			case REPL -> ScriptExecutor.repl(CLArgs.showAST());
			default -> throw new CommandLineArgumentException("Mode could not be inferred");
		}
	}
}
