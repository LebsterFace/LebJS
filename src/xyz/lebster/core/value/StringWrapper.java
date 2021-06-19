package xyz.lebster.core.value;

import xyz.lebster.core.value.prototype.StringPrototype;

public class StringWrapper extends LiteralWrapper<StringLiteral> {
	public StringWrapper(StringLiteral s) {
		super(s);
		set("length", new NumericLiteral(s.value.length()));
	}

	@Override
	public StringPrototype getPrototype() {
		return StringPrototype.instance;
	}
}
