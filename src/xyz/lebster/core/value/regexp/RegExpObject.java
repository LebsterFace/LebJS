package xyz.lebster.core.value.regexp;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.value.HasBuiltinTag;
import xyz.lebster.core.value.object.ObjectValue;

public final class RegExpObject extends ObjectValue implements HasBuiltinTag {
	public final String pattern;
	public final String flags;

	public RegExpObject(Intrinsics intrinsics, String pattern, String flags) {
		super(intrinsics.regExpPrototype);
		this.pattern = pattern;
		this.flags = flags;
	}

	@Override
	public String getBuiltinTag() {
		return "RegExp";
	}

	@Override
	public void display(StringRepresentation representation) {
		representation.append(ANSI.RED);
		representation.append('/');
		representation.append(pattern);
		representation.append('/');
		representation.append(flags);
		representation.append(ANSI.RESET);
	}

	@Override
	public boolean displayAsJSON() {
		return false;
	}
}
