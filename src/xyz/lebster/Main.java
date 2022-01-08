package xyz.lebster;

import xyz.lebster.cli.CLArgumentException;
import xyz.lebster.cli.CLArguments;
import xyz.lebster.cli.REPL;
import xyz.lebster.cli.Testing;
import xyz.lebster.core.ANSI;

public final class Main {
	public static void main(String[] args) {
		try {
			final CLArguments arguments = CLArguments.from(args);
			switch (arguments.mode()) {
				case File -> ScriptExecutor.file(arguments.filePathOrNull(), arguments.options());
				case REPL -> new REPL(arguments.options()).run();
				case GIF -> ScriptExecutor.gif(arguments.options());
				case Tests -> Testing.test(arguments.options());
			}
		} catch (CLArgumentException e) {
			System.err.print(ANSI.BRIGHT_RED);
			System.err.print(e.getMessage());
			System.err.println(ANSI.RESET);
		}
	}
}