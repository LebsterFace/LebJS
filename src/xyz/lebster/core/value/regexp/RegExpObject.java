package xyz.lebster.core.value.regexp;

import xyz.lebster.core.interpreter.Intrinsics;
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
}
