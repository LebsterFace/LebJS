package xyz.lebster.core.node.value;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.runtime.prototype.StringPrototype;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string-objects")
public final class StringWrapper extends PrimitiveWrapper<StringLiteral> {
	public StringWrapper(StringLiteral s) {
		super(s);
		put("length", new NumericLiteral(s.value.length()));
	}

	@Override
	public StringPrototype getPrototype() {
		return StringPrototype.instance;
	}
}