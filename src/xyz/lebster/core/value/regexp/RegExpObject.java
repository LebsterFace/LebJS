package xyz.lebster.core.value.regexp;

import xyz.lebster.core.value.HasBuiltinTag;
import xyz.lebster.core.value.object.ObjectValue;

public final class RegExpObject extends ObjectValue implements HasBuiltinTag {
	public final String pattern;

	public RegExpObject(RegExpPrototype prototype, String pattern) {
		super(prototype);
		this.pattern = pattern;
	}

	@Override
	public String getBuiltinTag() {
		return "RegExp";
	}
}
