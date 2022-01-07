package xyz.lebster.core.node.value.object;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.node.value.NumberValue;
import xyz.lebster.core.node.value.StringValue;
import xyz.lebster.core.runtime.prototype.StringPrototype;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string-objects")
public final class StringWrapper extends PrimitiveWrapper<StringValue> {
	public StringWrapper(StringValue s) {
		super(s);
		put("length", new NumberValue(s.value.length()));
	}

	@Override
	public StringPrototype getPrototype() {
		return StringPrototype.instance;
	}
}