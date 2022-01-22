package xyz.lebster.core.runtime.value.object;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.runtime.Names;
import xyz.lebster.core.runtime.value.HasBuiltinTag;
import xyz.lebster.core.runtime.value.primitive.NumberValue;
import xyz.lebster.core.runtime.value.primitive.StringValue;
import xyz.lebster.core.runtime.value.prototype.StringPrototype;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-string-objects")
public final class StringWrapper extends PrimitiveWrapper<StringValue> implements HasBuiltinTag {
	public StringWrapper(StringValue s) {
		super(s);
		this.put(Names.length, new NumberValue(s.value.length()));
	}

	@Override
	public ObjectValue getDefaultPrototype() {
		return StringPrototype.instance;
	}

	@Override
	public String getBuiltinTag() {
		return "String";
	}
}