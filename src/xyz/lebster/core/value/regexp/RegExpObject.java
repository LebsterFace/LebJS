package xyz.lebster.core.value.regexp;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.HasBuiltinTag;
import xyz.lebster.core.value.object.ObjectValue;

import java.util.regex.Pattern;

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
		this.pattern = Pattern.compile(source, bitfield);
		this.source = source;
		this.flags = flags;
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
