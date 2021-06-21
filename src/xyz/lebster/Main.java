package xyz.lebster;

import xyz.lebster.core.value.Dictionary;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";

	public static void main(String[] args) {
		final CommandLineArgs CLArgs = CommandLineArgs.fromArgs(args);

		switch (CLArgs.mode()) {
			case File -> ScriptExecutor.executeFileWithHandling(Path.of(CLArgs.fileName()), ScriptExecutor.getDefaultGlobalObject(), CLArgs.showAST());
			case Tests -> Testing.test();
			case REPL -> ScriptExecutor.repl(CLArgs.showAST());
			default -> throw new CommandLineArgumentException("Mode could not be inferred");
		}
	}
}
