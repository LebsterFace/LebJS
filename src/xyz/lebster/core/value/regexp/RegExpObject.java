package xyz.lebster.core.value.regexp;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.NonCompliant;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.HasBuiltinTag;
import xyz.lebster.core.value.object.ObjectValue;

import java.util.regex.Pattern;

@NonCompliant
public final class RegExpObject extends ObjectValue implements HasBuiltinTag {
	public final Pattern pattern;
	public final String source;
	public final String flags;

	public RegExpObject(Intrinsics intrinsics, String source, String flags) {
		super(intrinsics.regExpPrototype);
		// TODO: Other flags
		// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Regular_expressions#advanced_searching_with_flags
		int bitfield = 0;
		// d - Generate indices for substring matches.
		// g - Global search.
		if (flags.contains("i")) bitfield |= Pattern.CASE_INSENSITIVE; // i - Case-insensitive search.
		if (flags.contains("m")) bitfield |= Pattern.MULTILINE; // m - Allows ^ and $ to match newline characters.
		if (flags.contains("s")) bitfield |= Pattern.DOTALL; // s - Allows . to match newline characters.
		// u - "Unicode"; treat a pattern as a sequence of Unicode code points.
		// y - Perform a "sticky" search that matches starting at the current position in the target string.
		this.source = source;
		this.flags = flags;
		this.pattern = Pattern.compile(sourceForPattern(), bitfield);
	}

	/**
	 * Returns the source in a {@link java.util.regex.Pattern}-compatible form
	 * NOTE: This still isn't perfect; there are features of Patterns which
	 * should not be exposed by RegExpObjects.
	 * */
	private String sourceForPattern() {
		return source
			.replaceAll("\\[]", "(?!)") // Empty class -> negative lookahead
			.replaceAll("\\[\\^]", "(?:\\\\u000D\\\\u000A|.|[\\\\u000A\\\\u000B\\\\u000C\\\\u000D\\\\u0085\\\\u2028\\\\u2029])"); // Negated empty class -> any char class
	}

	@Override
	public String getBuiltinTag() {
		return "RegExp";
	}

	@Override
	public void display(StringBuilder builder) {
		builder.append(ANSI.RED);
		builder.append('/');
		builder.append(source);
		builder.append('/');
		builder.append(flags);
		builder.append(ANSI.RESET);
	}

	@Override
	public boolean displayAsJSON() {
		return false;
	}

	public boolean isGlobal() {
		return this.flags.contains("g");
	}
}
