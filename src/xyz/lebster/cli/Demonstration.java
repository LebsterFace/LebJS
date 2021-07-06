package xyz.lebster.cli;

import xyz.lebster.node.value.Dictionary;

import java.util.concurrent.TimeUnit;

public class Demonstration {
	public static final String[] commands = new String[] {
		"1 + 2 * 3",
		"function square(x) { return x * x }",
		"let num = square(1 + 1);",
		"num",
		"globalThis",
		"globalThis[\"squ\" + \"are\"](num)"
	};

	public static void type(String str, TimeUnit unit, long delay) throws InterruptedException {
		TimeUnit.MILLISECONDS.sleep(100);

		for (char ch : str.toCharArray()) {
			System.out.print(ch);
			unit.sleep(delay);
		}

		TimeUnit.MILLISECONDS.sleep(100);
		System.out.println();
	}

	public static void demonstrate(Dictionary globalObject, ExecutionOptions options) {
		System.out.println("Starting REPL...");
		for (String command : commands) {
			System.out.print("> ");
			try {
				type(command, TimeUnit.MILLISECONDS, 50);
				if (command.isBlank()) continue;
				ScriptExecutor.executeWithHandling(command, globalObject, options);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
	}
}
