package xyz.lebster.core.node.value;

import xyz.lebster.core.runtime.prototype.StringPrototype;

public final class StringWrapper extends LiteralWrapper<StringLiteral> {
	public StringWrapper(StringLiteral s) {
		super(s);
		put("length", new NumericLiteral(s.value.length()));
	}

	@Override
	public StringPrototype getPrototype() {
		return StringPrototype.instance;
	}
}