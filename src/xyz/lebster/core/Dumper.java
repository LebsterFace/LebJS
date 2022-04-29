package xyz.lebster.core;

import xyz.lebster.core.node.Dumpable;

@Deprecated
public final class Dumper {
	private Dumper() {
	}

	public static void dumpIndent(int indent) {
		System.out.print("  ".repeat(indent));
	}

	public static void dumpName(int indent, String name) {
		dumpIndent(indent);
		System.out.printf("%s%s%s:%n", ANSI.BRIGHT_GREEN, name, ANSI.RESET);
	}

	public static void dumpParameterized(int indent, String name, String param) {
		dumpIndent(indent);
		System.out.printf("%s%s %s%s%s:%n", ANSI.BRIGHT_GREEN, name, ANSI.BRIGHT_YELLOW, param, ANSI.RESET);
	}

	public static void dumpValue(int indent, String name, String value) {
		dumpIndent(indent);
		System.out.printf("%s%s %s%s%s%n", ANSI.CYAN, name, ANSI.RESET, value, ANSI.RESET);
	}

	public static void dumpValue(int indent, String value) {
		dumpIndent(indent);
		System.out.printf("%s%s%s%n", ANSI.CYAN, value, ANSI.RESET);
	}

	public static void dumpSingle(int indent, String value) {
		dumpIndent(indent);
		System.out.printf("%s%s%s%n", ANSI.BRIGHT_GREEN, value, ANSI.RESET);
	}

	public static void dumpEnum(int indent, Enum<?> value) {
		dumpIndent(indent);
		System.out.printf("%s%s %s%s%s%n", ANSI.BRIGHT_RED, value.getClass().getSimpleName(), ANSI.BRIGHT_YELLOW, value, ANSI.RESET);
	}

	public static void dumpEnum(int indent, String indicator, Enum<?> value) {
		dumpIndicator(indent, indicator);
		dumpEnum(indent + 1, value);
	}

	public static void dumpIndicated(int indent, String indicator, Dumpable node) {
		dumpIndicator(indent, indicator);
		node.dump(indent + 1);
	}

	public static void dumpIndicator(int indent, String indicator) {
		dumpIndent(indent);
		System.out.printf("%s(%s)%s%n", ANSI.BRIGHT_MAGENTA, indicator, ANSI.RESET);
	}
}